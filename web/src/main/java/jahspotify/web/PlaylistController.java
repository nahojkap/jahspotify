package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.Library;
import jahspotify.media.Link;
import jahspotify.media.Playlist;
import jahspotify.services.JahSpotifyService;
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
            Playlist playlist = _jahSpotifyService.getJahSpotify().readPlaylist(playlistLink, index, entries);
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
        webPlaylist.setTracks(toWebLinks(playlist.getTracks()));
        webPlaylist.setNumTracks(playlist.getNumTracks());
        webPlaylist.setIndex(playlist.getIndex());
        return webPlaylist;
    }

    @RequestMapping(value = "/folder/{link}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public jahspotify.web.media.Library.Entry retrieveFolder(@PathVariable String link, @RequestParam(defaultValue = "0") int levels)
    {
        try
        {
            final Link uri = Link.create(link);
            Library.Entry entry = _jahSpotifyService.getJahSpotify().readFolder(uri, levels);
            final jahspotify.web.media.Library.Entry entry1 = convertToWebEntry(entry);
            return entry1;
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving folder: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    private jahspotify.web.media.Library.Entry convertToWebEntry(final Library.Entry folderEntry)
    {
        final jahspotify.web.media.Library.Entry webFolderEntry = new jahspotify.web.media.Library.Entry(new jahspotify.web.media.Link(folderEntry.getId(), jahspotify.web.media.Link.Type.valueOf(folderEntry.getType())), folderEntry.getName(), folderEntry.getType());
        webFolderEntry.setParentID(folderEntry.getParentID());
        webFolderEntry.setNumSubEntries(folderEntry.getNumEntries());
        for (Library.Entry subEntry : folderEntry.getSubEntries())
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
            final Library library = _jahSpotifyService.getJahSpotify().retrieveLibrary();
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
        for (Library.Entry entry : library.getEntries())
        {
            webLibrary.addEntry(convertToWebEntry(entry));
        }
        return webLibrary;
    }

    @RequestMapping(value = "/ajax/library/", method = RequestMethod.GET, produces = "application/json")
    public JSTreeNode retrieveLibraryAjax(final HttpServletResponse httpServletResponse)
    {
        final Library library = _jahSpotifyService.getJahSpotify().retrieveLibrary();

        JSTreeNode rootNode = new JSTreeNode();
        rootNode.setState("open");
        final JSTreeNode.JSTreeNodeData data = new JSTreeNode.JSTreeNodeData();
        data.setTitle("ROOT");
        rootNode.setData(data);
        final HashMap<String, String> attr = new HashMap<String, String>();
        attr.put("id", "ROOT");
        rootNode.setAttr(attr);

        final List<Library.Entry> children = library.getEntries();
        for (Library.Entry child : children)
        {
            rootNode.addChild(processEntry(child));
        }
        return rootNode;
    }

    private JSTreeNode processEntry(Library.Entry entry)
    {
        JSTreeNode jsTreeNode = new JSTreeNode();
        // Retrieve the playlist

        final JSTreeNode.JSTreeNodeData data = new JSTreeNode.JSTreeNodeData();
        data.setTitle(entry.getName());
        jsTreeNode.setData(data);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", entry.getId());
        map.put("rel", entry.getType());
        jsTreeNode.setAttr(map);

        for (Library.Entry subEntry : entry.getSubEntries())
        {
            final JSTreeNode subJSTreeNode = processEntry(subEntry);
            if (subJSTreeNode != null)
            {
                jsTreeNode.addChild(subJSTreeNode);
            }
        }

        return jsTreeNode;

    }

}
