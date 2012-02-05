package jahspotify.web;

import javax.servlet.http.*;

import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
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
                return;
            }

            jahspotify.web.media.Artist webArtist = convertToWebArtist(artist);

            super.writeResponseGeneric(httpServletResponse,webArtist);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving artist: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    private jahspotify.web.media.Artist convertToWebArtist(final Artist artist)
    {
        jahspotify.web.media.Artist webArtist = new jahspotify.web.media.Artist();
        webArtist.setAlbums(toWebLinks(artist.getAlbums()));
        webArtist.setPortraits(toWebLinks(artist.getPortraits()));
        webArtist.setSimilarArtists(toWebLinks(artist.getSimilarArtists()));
        webArtist.setId(toWebLink(artist.getId()));
        // webArtist.setRestrictions(toWebRestrictions(artist.getRestrictions()));

        BeanUtils.copyProperties(artist, webArtist, new String[]{"id", "restrictions", "albums", "similarArtists", "portraits"});


        return webArtist;
    }

}
