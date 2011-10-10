package jahspotify.web;

import java.io.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.*;
import jahspotify.service.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class AlbumController extends BaseController
{
    private Log _log = LogFactory.getLog(AlbumController.class);

   @RequestMapping(value = "/album/*", method = RequestMethod.GET)
    public void retrieveAlbum(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link link = retrieveLink(httpServletRequest);
            Album album = _jahSpotifyService.getJahSpotify().readAlbum(link);
            if (album == null)
            {
                super.writeMediaNotReadable(httpServletResponse);
            }
            else
            {
                _log.debug("Got album: " + album);
                super.writeResponseGeneric(httpServletResponse,album);
            }
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving album: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

}
