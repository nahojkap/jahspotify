package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.*;
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
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        Playlist playlist = _jahSpotifyService.getJahSpotify().readPlaylist(uri);
        _log.debug("Got playlist: " + playlist);
        writeResponseGeneric(httpServletResponse, playlist);
    }

    @RequestMapping(value = "/library/", method = RequestMethod.GET)
    public void retrieveLibrary(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final Library library = _jahSpotifyService.getJahSpotify().retrieveLibrary();

        JSTreeNode rootNode = new JSTreeNode();
        rootNode.setState("open");
        rootNode.setData("ROOT");
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

        jsTreeNode.setData(entry.getId());
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", entry.getId());
        map.put("rel",entry.getType());
        jsTreeNode.setAttr(map);

        for (Library.Entry subEntry : entry.getSubEntries())
        {
            final JSTreeNode subJSTreeNode = processEntry(entry);
            if (subJSTreeNode != null)
            {
                jsTreeNode.addChild(jsTreeNode);
            }
        }

        return jsTreeNode;

    }

}
