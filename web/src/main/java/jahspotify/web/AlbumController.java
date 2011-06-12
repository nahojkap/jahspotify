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
    public void testPlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        Album album = _jahSpotifyService.getJahSpotify().readAlbum(uri);
        _log.debug("Got album: " + album);
        super.writeResponseGeneric(httpServletResponse,album);
    }
}
