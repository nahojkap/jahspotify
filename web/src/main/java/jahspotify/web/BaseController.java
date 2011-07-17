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

    @Value(value = "${jahspotify.web.controller.default-media-expires-duration}")
    private int _defaultMediaExpirationTime;

    protected void writeResponse(final HttpServletResponse httpServletResponse, final SimpleStatusResponse simpleStatusResponse)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            _log.debug("Serializing: " + simpleStatusResponse);
            final PrintWriter writer = httpServletResponse.getWriter();
            gson.toJson(simpleStatusResponse.getResponseStatus(), writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(),e);
        }
    }

    protected void writeMediaNotReadable(final HttpServletResponse httpServletResponse)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.RESOURCE_NOT_FOUND);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    protected void writeResponseGeneric(final HttpServletResponse httpServletResponse, final Object object)
    {
        this.writeResponseGenericWithDate(httpServletResponse,null,object);
    }


    protected void writeResponseGenericWithDate(final HttpServletResponse httpServletResponse, final Date lastModified, final Object object)
    {
        writeResponseGenericWithDate(httpServletResponse,lastModified, _defaultMediaExpirationTime,object);

    }

    protected void writeResponseGenericWithDate(final HttpServletResponse httpServletResponse, final Date lastModified, final int expirationTime, final Object object)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            if (lastModified != null)
            {
                httpServletResponse.addHeader("Expires", createDateHeader(expirationTime));
                httpServletResponse.addHeader("Last-Modified", toHttpDate(lastModified));
            }
            _log.debug("Serializing: " + object);
            final PrintWriter writer = httpServletResponse.getWriter();
            gson.toJson(object,writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(),e);
        }
    }


    protected String createDateHeader(int expires)
    {
        final Calendar utc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.add(Calendar.SECOND, expires);
        return toHttpDate(utc.getTime());
    }

    protected String toHttpDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
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
        final BufferedReader br = new BufferedReader(httpServletRequest.getReader());
        Gson gson = new Gson();
        return gson.fromJson(br,classOfT);
    }

    public void writeErrorResponse(final HttpServletResponse httpServletResponse, final Exception e)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }
}
