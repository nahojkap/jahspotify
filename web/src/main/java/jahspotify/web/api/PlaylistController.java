package jahspotify.web.api;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.Library;
import jahspotify.media.Link;
import jahspotify.media.Playlist;
import jahspotify.services.JahSpotifyService;
import jahspotify.web.media.LibraryEntry;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class PlaylistController extends BaseController
{
    private Log _log = LogFactory.getLog(PlaylistController.class);

    @Autowired
    JahSpotifyService _jahSpotifyService;

    @RequestMapping(value = "/playlist/{link}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Playlist retrievePlaylist(@PathVariable String link, @RequestParam(defaultValue = "0") int entries, @RequestParam(defaultValue = "0") int index, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link playlistLink = Link.create(link);
            Playlist playlist = _jahSpotify.readPlaylist(playlistLink, index, entries);
            return convertToWebPlaylist(playlist);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving playlist: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    private jahspotify.web.media.Playlist convertToWebPlaylist(final Playlist playlist)
    {
        jahspotify.web.media.Playlist webPlaylist = new jahspotify.web.media.Playlist();
        webPlaylist.setAuthor(playlist.getAuthor());
        webPlaylist.setCollaborative(playlist.isCollaborative());
        webPlaylist.setDescription(playlist.getDescription());
        webPlaylist.setId(toWebLink(playlist.getId()));
        webPlaylist.setPicture((playlist.getPicture() != null ? playlist.getPicture().asString() : null));
        webPlaylist.setName(playlist.getName());
        webPlaylist.setTracks(toWebLinksAsList(playlist.getTracks()));
        webPlaylist.setNumTracks(playlist.getNumTracks());
        webPlaylist.setIndex(playlist.getIndex());
        return webPlaylist;
    }

    @RequestMapping(value = "/folder/{link}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public LibraryEntry retrieveFolder(@PathVariable String link, @RequestParam(defaultValue = "0") int levels)
    {
        try
        {
            final Link uri = Link.create(link);
            jahspotify.media.LibraryEntry entry = _jahSpotify.readFolder(uri, levels);
            final LibraryEntry entry1 = convertToWebEntry(entry);
            return entry1;
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving folder: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    private LibraryEntry convertToWebEntry(final jahspotify.media.LibraryEntry folderEntry)
    {
        final LibraryEntry webFolderEntry = new LibraryEntry(new jahspotify.web.media.Link(folderEntry.getId(), jahspotify.web.media.Link.Type.valueOf(folderEntry.getType())), folderEntry.getName(), folderEntry.getType());
        webFolderEntry.setParentID(folderEntry.getParentID());
        webFolderEntry.setNumSubEntries(folderEntry.getNumEntries());
        for (jahspotify.media.LibraryEntry subEntry : folderEntry.getSubEntries())
        {
            webFolderEntry.addSubEntry(convertToWebEntry(subEntry));
        }
        return webFolderEntry;
    }

    @RequestMapping(value = "/library/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Library retrieveLibrary()
    {
        try
        {
            final Library library = _jahSpotify.retrieveLibrary();
            return convertToWebLibrary(library);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving library: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    private jahspotify.web.media.Library convertToWebLibrary(final Library library)
    {
        if (library == null)
        {
            return null;
        }
        jahspotify.web.media.Library webLibrary = new jahspotify.web.media.Library();
        webLibrary.setOwner(library.getOwner());
        for (jahspotify.media.LibraryEntry entry : library.getEntries())
        {
            webLibrary.addEntry(convertToWebEntry(entry));
        }
        return webLibrary;
    }

}
