package jahspotify.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.*;
import jahspotify.media.Album;
import jahspotify.media.Disc;
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

            if (album == null)
            {
                super.writeMediaNotReadable(httpServletResponse);
                return;
            }
            jahspotify.web.media.Album webAlbum = convertToWebAlbum(album);
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
        webAlbum.setDiscs(toWebDiscs(album.getDiscs()));
        webAlbum.setType(jahspotify.web.media.AlbumType.valueOf(album.getType().name()));
        // webAlbum.setRestrictions(toWebRestrictions(album.getRestrictions()));

        BeanUtils.copyProperties(album, webAlbum, new String[]{ "id", "restrictions", "artist", "cover", "type", "discs"  });
        return webAlbum;
    }

    private List<jahspotify.web.media.Disc> toWebDiscs(final List<Disc> discs)
    {
        List<jahspotify.web.media.Disc> webDiscs = new ArrayList<jahspotify.web.media.Disc>();
        for (Disc disc : discs)
        {
            webDiscs.add(toWebDisc(disc));
        }
        return null;
    }

    private jahspotify.web.media.Disc toWebDisc(final Disc disc)
    {
        jahspotify.web.media.Disc webDisc = new jahspotify.web.media.Disc();
        webDisc.setTracks(toWebLinks(disc.getTracks()));
        return null;
    }

}
