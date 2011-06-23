package jahspotify.web;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.Link;
import jahspotify.service.JahSpotifyService;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;

/**
 * @author Johan Lindquist
 */
public class BaseController
{
    private Log _log = LogFactory.getLog(BaseController.class);

    @Autowired
    protected JahSpotifyService _jahSpotifyService;

    @Value(value = "${jahspotify.web.controller.track-expires-duration}")
    private int _trackExpiresDuration;

    protected void writeResponse(final HttpServletResponse httpServletResponse, final SimpleStatusResponse simpleStatusResponse)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");

            _log.debug("Serializing: " + simpleStatusResponse);

            final PrintWriter writer = httpServletResponse.getWriter();
            final String s = gson.toJson(simpleStatusResponse);
            _log.debug("Serialized: " + s);

            writer.write(s);

            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void writeResponseGeneric(final HttpServletResponse httpServletResponse, final Object object)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            httpServletResponse.addHeader("Expires", createExpiresHeader(_trackExpiresDuration)); // 5 minutes for now
            _log.debug("Serializing: " + object);
            final PrintWriter writer = httpServletResponse.getWriter();
            final String s = gson.toJson(object);
            _log.debug("Serialized: " + s);
            writer.write(s);

            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected String createExpiresHeader(int expires)
    {
        final Calendar utc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.add(Calendar.SECOND,expires);
        return toHttpDate(utc.getTime());
    }

    protected String toHttpDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        return simpleDateFormat.format(date);
    }

    protected Link retrieveLink(final HttpServletRequest httpServletRequest)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        return Link.create(uri);
    }

    protected <T> T readRequest(final HttpServletRequest httpServletRequest, final Class<T> classOfT) throws IOException
    {
        BufferedReader br = new BufferedReader(httpServletRequest.getReader());
        StringBuffer sb = new StringBuffer();

        String tmp = br.readLine();
        while (tmp != null)
        {
            sb.append(tmp);
            sb.append("\n");
            tmp = br.readLine();
        }

        Gson gson = new Gson();
        return gson.fromJson(sb.toString(), classOfT);
    }



}
