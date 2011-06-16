package jahspotify.web;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import jahspotify.media.*;
import jahspotify.service.JahSpotifyService;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class ImageController extends BaseController
{
    private Log _log = LogFactory.getLog(ImageController.class);

    @RequestMapping(value = "/image/*", method = RequestMethod.GET)
    public void retrieveImage(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);

            // FIXME: This image should be cached

            final Image image = _jahSpotifyService.getJahSpotify().readImage(uri);
            byte[] bytes = image.getBytes();

            httpServletResponse.setContentType("image/jpeg");
            httpServletResponse.setContentLength(bytes.length);

            final ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}
