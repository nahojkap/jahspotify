package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.Artist;
import jahspotify.services.JahSpotifyService;
import jahspotify.web.media.*;
import jahspotify.web.media.Track;
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
            boolean shipFullTrack = Boolean.parseBoolean(httpServletRequest.getParameter("full"));
            final jahspotify.media.Link uri = retrieveLink(httpServletRequest);
            final jahspotify.media.Track track = _jahSpotifyService.getJahSpotify().readTrack(uri);

            if (track == null)
            {
                super.writeMediaNotReadable(httpServletResponse);
                return;
            }

            if (shipFullTrack)
            {
                FullTrack fullWebTrack = createFullTrack(track);
                writeResponseGenericWithDate(httpServletResponse, track.getLastModified(), _trackExpirationTime, fullWebTrack);
            }
            else
            {
                jahspotify.web.media.Track webTrack = convertToWebTrack(track);
                writeResponseGenericWithDate(httpServletResponse, track.getLastModified(), _trackExpirationTime, webTrack);
            }
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving track: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);

        }
    }

    private FullTrack createFullTrack(final jahspotify.media.Track track)
    {
        FullTrack fullTrack = new FullTrack();
        fullTrack.setId(toWebLink(track.getId()));
        fullTrack.setTitle(track.getTitle());
        fullTrack.setLength(track.getLength());
        fullTrack.setTrackNumber(track.getTrackNumber());
        fullTrack.setExplicit(track.isExplicit());

        final List<jahspotify.media.Link> artists = track.getArtists();
        final List<jahspotify.web.media.Link> webArtistLinks = new ArrayList<jahspotify.web.media.Link>();

        final List<String> webArtistNames = new ArrayList<String>();
        fullTrack.setArtistLinks(webArtistLinks);
        fullTrack.setArtistNames(webArtistNames);

        for (jahspotify.media.Link artistLink : artists)
        {
            final Artist artist = _jahSpotifyService.getJahSpotify().readArtist(artistLink);

            if (artist != null)
            {
                webArtistLinks.add(toWebLink(artistLink));
                webArtistNames.add(artist.getName());
            }
            else
            {
                // at least one artist has not loaded yet - let the client know
                fullTrack.setComplete(false);
            }
        }

        final jahspotify.media.Album album = _jahSpotifyService.getJahSpotify().readAlbum(track.getAlbum());
        if (album != null)
        {
            fullTrack.setAlbumName(album.getName());
            fullTrack.setAlbumLink(toWebLink(album.getId()));
            fullTrack.setAlbumCoverLink(toWebLink(album.getCover()));
        }
        else
        {
            fullTrack.setComplete(false);
        }




        return fullTrack;
    }

    private Track convertToWebTrack(final jahspotify.media.Track track)
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
