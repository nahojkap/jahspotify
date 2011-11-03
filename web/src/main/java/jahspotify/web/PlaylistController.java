package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.*;
import jahspotify.media.Library;
import jahspotify.service.*;
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

    @RequestMapping(value = "/playlist/*", method = RequestMethod.GET)
    public void retrievePlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);
            Playlist playlist = _jahSpotifyService.getJahSpotify().readPlaylist(uri);
            _log.debug("Got playlist: " + playlist);
            writeResponseGeneric(httpServletResponse, playlist);
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving playlist: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    @RequestMapping(value = "/folder/*", method = RequestMethod.GET)
    public void retrieveFolder(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);

            int level = (httpServletRequest.getParameter("level") == null ? 0 : Integer.parseInt(httpServletRequest.getParameter("level")));

            _log.debug("Retriving folder:" + uri + " (to level " + level +")");
            Library.Entry folderEntry = _jahSpotifyService.getJahSpotify().readFolder(uri, level);
            _log.debug("Got folder: " + folderEntry);

            jahspotify.web.media.Library.Entry rootFolder = convertToWebEntry(folderEntry);

            writeResponseGeneric(httpServletResponse, rootFolder);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            super.writeErrorResponse(httpServletResponse, e);

        }
    }

    private jahspotify.web.media.Library.Entry convertToWebEntry(final Library.Entry folderEntry)
    {
        final jahspotify.web.media.Library.Entry webFolderEntry = new jahspotify.web.media.Library.Entry(folderEntry.getId(), folderEntry.getName(), folderEntry.getType());
        webFolderEntry.setParentID(folderEntry.getParentID());
        for (Library.Entry subEntry : folderEntry.getSubEntries())
        {
            webFolderEntry.addSubEntry(convertToWebEntry(subEntry));
        }
        return webFolderEntry;
    }

    @RequestMapping(value = "/library/", method = RequestMethod.GET)
    public void retrieveLibrary(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final Library library = _jahSpotifyService.getJahSpotify().retrieveLibrary();
        final jahspotify.web.media.Library webLibrary = convertToWebLibrary(library);
        writeResponseGeneric(httpServletResponse, webLibrary);
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

    @RequestMapping(value = "/ajax/library/", method = RequestMethod.GET)
    public void retrieveLibraryAjax(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
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

        writeResponseGeneric(httpServletResponse, rootNode);


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
