package jahspotify.web.api;


import java.util.*;

import jahspotify.media.*;
import jahspotify.services.JahSpotifyService;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/track")
public class TrackController extends BaseController
{
    private Log _log = LogFactory.getLog(PlaylistController.class);

    @Autowired
    private JahSpotifyService _jahSpotifyService;

    @Value(value = "${jahspotify.web.controller.track-expires-duration}")
    private int _trackExpirationTime;

    @RequestMapping(value = "/{link}", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Media retrieveTrack(@PathVariable String link, @RequestParam(value="full", required = false) boolean shipFullTrack)
    {
        try
        {
            final jahspotify.media.Link uri = Link.create(link);
            final jahspotify.media.Track track = _jahSpotify.readTrack(uri);

            if (shipFullTrack)
            {
                return createFullTrack(track);
            }
            else
            {
                return convertToWebTrack(track);
            }
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving track: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

}
