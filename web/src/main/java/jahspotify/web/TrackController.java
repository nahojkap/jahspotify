package jahspotify.web;

import javax.servlet.http.*;

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
public class TrackController extends BaseController
{
    private Log _log = LogFactory.getLog(PlaylistController.class);

    @Autowired
    private JahSpotifyService _jahSpotifyService;

    @Value(value = "${jahspotify.web.controller.track-expires-duration}")
    private int _trackExpirationTime;

    @RequestMapping(value = "/track/*", method = RequestMethod.GET)
    public void retrieveTrack(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);
            final Track track = _jahSpotifyService.getJahSpotify().readTrack(uri);

            if (track == null)
            {
                super.writeMediaNotReadable(httpServletResponse);
                return;
            }

            jahspotify.web.media.Track webTrack = convertToWebTrack(track);

            writeResponseGenericWithDate(httpServletResponse, track.getLastModified(), _trackExpirationTime, webTrack);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving track: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);

        }
    }

    private jahspotify.web.media.Track convertToWebTrack(final Track track)
    {
        jahspotify.web.media.Track webTrack = new jahspotify.web.media.Track();

        webTrack.setAlbum(toWebLink(track.getAlbum()));
        webTrack.setArtists(toWebLinks(track.getArtists()));
        webTrack.setId(toWebLink(track.getId()));
        // webTrack.setRestrictions(toWebRestrictions(track.getRestrictions()));

        BeanUtils.copyProperties(track, webTrack,new String[] { "id", "restrictions", "album", "artists" });

        return webTrack;

    }

}
