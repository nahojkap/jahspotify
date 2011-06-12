package jahspotify.web;

import java.io.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class ArtistController extends BaseController
{
    private Log _log = LogFactory.getLog(ArtistController.class);

    @RequestMapping(value = "/artist/*", method = RequestMethod.GET)
    public void testPlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        Artist artist = _jahSpotifyService.getJahSpotify().readArtist(uri);
        _log.debug("Got artist: " + artist);
        super.writeResponseGeneric(httpServletResponse,artist);
    }

}
