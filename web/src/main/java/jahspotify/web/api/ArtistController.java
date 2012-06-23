package jahspotify.web.api;

import jahspotify.media.Artist;
import jahspotify.media.Link;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/artist")
public class ArtistController extends BaseController
{
    private Log _log = LogFactory.getLog(ArtistController.class);

    @RequestMapping(value = "/{link}", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Artist retrieveArtist(@PathVariable String link)
    {
        try
        {
            Link artistLink = Link.create(link);
            Artist artist = _jahSpotify.readArtist(artistLink);
            return convertToWebArtist(artist);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving artist: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }


}
