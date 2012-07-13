package jahspotify.web.api;

import java.util.*;

import jahspotify.media.Album;
import jahspotify.media.Disc;
import jahspotify.media.Link;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping("/album")
public class AlbumController extends BaseController
{
    private Log _log = LogFactory.getLog(AlbumController.class);

    @RequestMapping(value = "/{link}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Album retrieveAlbum(@PathVariable(value = "link") String link)
    {
        try
        {
            final Link albumLink = Link.create(link);
            Album album = _jahSpotify.readAlbum(albumLink);
            return convertToWebAlbum(album);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving album: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

}
