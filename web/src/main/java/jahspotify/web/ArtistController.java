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
    public void retrieveArtist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            Link uri = retrieveLink(httpServletRequest);
            Artist artist = _jahSpotifyService.getJahSpotify().readArtist(uri);
            if (artist == null)
            {
                super.writeMediaNotReadable(httpServletResponse);
            }
            _log.debug("Got artist: " + artist);
            super.writeResponseGeneric(httpServletResponse,artist);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
