package jahspotify.web;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.Link;
import jahspotify.services.JahSpotifyService;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Johan Lindquist
 */
public class BaseController
{
    protected Log _log = LogFactory.getLog(BaseController.class);

    @Autowired
    protected JahSpotifyService _jahSpotifyService;

    @Value(value = "${jahspotify.web.controller.default-media-expires-duration}")
    private int _defaultMediaExpirationTime;

    protected jahspotify.web.media.Link toWebLink(final Link link)
    {
        return new jahspotify.web.media.Link(link.asString(), jahspotify.web.media.Link.Type.valueOf(link.getType().name()));
    }

    protected List<jahspotify.web.media.Link> toWebLinks(final List<Link> links)
    {
        if (links == null || links.isEmpty())
        {
            return null;
        }

        List<jahspotify.web.media.Link> stringLinks = new ArrayList<jahspotify.web.media.Link>(links.size());

        for (Link track : links)
        {
            stringLinks.add(toWebLink(track));
        }

        return stringLinks;
    }


    protected void writeResponse(final HttpServletResponse httpServletResponse, final SimpleStatusResponse simpleStatusResponse)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            _log.debug("Serializing: " + simpleStatusResponse);
            final PrintWriter writer = httpServletResponse.getWriter();
            gson.toJson(simpleStatusResponse, writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(), e);
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
        this.writeResponseGenericWithDate(httpServletResponse, null, object);
    }


    protected void writeResponseGenericWithDate(final HttpServletResponse httpServletResponse, final Date lastModified, final Object object)
    {
        writeResponseGenericWithDate(httpServletResponse, lastModified, _defaultMediaExpirationTime, object);

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
            gson.toJson(object, writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(), e);
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
        return gson.fromJson(br, classOfT);
    }

    public void writeErrorResponse(final HttpServletResponse httpServletResponse, final Exception e)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }


    @ExceptionHandler(JahSpotifyWebException.class)
    public void handleJahSpotifyWebException(JahSpotifyWebException ex, HttpServletRequest request, HttpServletResponse response)
    {
        writeErrorResponse(response, ex);
    }

}
