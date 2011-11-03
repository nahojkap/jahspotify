package jahspotify.web;

import java.io.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.*;
import jahspotify.media.Link;
import jahspotify.media.Track;
import jahspotify.service.*;
import jahspotify.web.media.*;
import org.apache.commons.logging.*;
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
        webTrack.setArtists(convertToStringLinks(track.getArtists()));
        webTrack.setCover(track.getCover());

        return webTrack;

    }

}
