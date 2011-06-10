package jahspotify.web;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import jahspotify.service.JahSpotifyService;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class ImageController
{
    private Log _log = LogFactory.getLog(ImageController.class);

    @Autowired
    private JahSpotifyService _jahSpotifyService;

    @RequestMapping(value = "/image/*", method = RequestMethod.GET)
    public void retrieveImage(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
            _log.debug("Extracted URI: " + uri);

            byte[] bytes = _jahSpotifyService.getJahSpotify().readImage(uri);

            httpServletResponse.setContentType("image/jpeg");
            httpServletResponse.setContentLength(bytes.length);

            final ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

}
