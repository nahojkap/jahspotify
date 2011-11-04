package jahspotify.web;

import java.io.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.*;
import jahspotify.media.Album;
import jahspotify.media.Link;
import jahspotify.service.*;
import jahspotify.web.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
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
    public void retrieveAlbum(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link link = retrieveLink(httpServletRequest);
            Album album = _jahSpotifyService.getJahSpotify().readAlbum(link);

            jahspotify.web.media.Album webAlbum = convertToWebAlbum(album);

            _log.debug("Got album: " + album);
            super.writeResponseGeneric(httpServletResponse, webAlbum);

        }
        catch (Exception e)
        {
            _log.error("Error while retrieving album: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    private jahspotify.web.media.Album convertToWebAlbum(final Album album)
    {

        jahspotify.web.media.Album webAlbum = new jahspotify.web.media.Album();

        webAlbum.setId(toWebLink(album.getId()));
        webAlbum.setCover(toWebLink(album.getCover()));
        webAlbum.setArtist(toWebLink(album.getArtist()));

        BeanUtils.copyProperties(album, webAlbum, new String[]{ "id", "artist", "cover"});
        return webAlbum;
    }

}
