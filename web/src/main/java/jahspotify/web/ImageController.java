package jahspotify.web;

import java.io.*;
import java.util.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class ImageController extends BaseController
{
    private Log _log = LogFactory.getLog(ImageController.class);
    private String _cacheLocation = "/var/lib/jahspotify/web/cache/images/";

    @RequestMapping(value = "/image/*", method = RequestMethod.GET)
    public void retrieveImage(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);

            if (uri == null)
            {
                final SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
                simpleStatusResponse.setResponseStatus(ResponseStatus.MISSING_PARAMETER);
                writeResponse(httpServletResponse, simpleStatusResponse);
                return;
            }

            if (!uri.isImageLink())
            {
                final SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
                simpleStatusResponse.setResponseStatus(ResponseStatus.INVALID_PARAMETER);
                writeResponse(httpServletResponse, simpleStatusResponse);
                return;
            }


            Image image = getFromCache(uri);
            if (image == null)
            {
                image = _jahSpotifyService.getJahSpotify().readImage(uri);
            }

            if (image != null)
            {
                byte[] bytes = image.getBytes();

                cacheImage(uri, bytes);

                httpServletResponse.setContentType("image/jpeg");

                // Setup caching parameters
                httpServletResponse.addHeader("Last-Modified", "Tue, 6 Sep 2005 15:10:00 UTC");

                final Calendar utc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.add(Calendar.YEAR, 1);

                httpServletResponse.addHeader("Expires", toHttpDate(utc.getTime()));

                httpServletResponse.setContentLength(bytes.length);

                final ServletOutputStream outputStream = httpServletResponse.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
            }

        }
        catch (Exception e)
        {
            _log.error("Error while service image: " + e.getMessage(), e);
            final SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, simpleStatusResponse);
        }


    }

    private Image getFromCache(final Link uri)
    {
        final String substring = uri.asString().substring(uri.asString().lastIndexOf(":") + 1);
        File f = new File(_cacheLocation + substring);
        byte[] bytes;
        if (f.exists())
        {
            _log.debug("Image found in cache: " + uri);

            try
            {
                FileInputStream fileInputStream = new FileInputStream(f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[16000];
                int len = fileInputStream.read(buffer,0,16000);
                while (len != -1)
                {
                    baos.write(buffer,0,len);
                    len = fileInputStream.read(buffer,0,16000);
                }
                _log.debug("Image size in cache: " + baos.toByteArray().length);

                return new Image(uri,baos.toByteArray());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void cacheImage(final Link uri, final byte[] bytes)
    {
        final String substring = uri.asString().substring(uri.asString().lastIndexOf(":") + 1);
        new File(_cacheLocation).mkdirs();
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(_cacheLocation + substring);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
