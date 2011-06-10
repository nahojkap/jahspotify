package jahspotify.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.*;
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

        final List<Container> children = library.getChildren();
        for (Container child : children)
        {
            rootNode.addChild(processContainer(child));
        }

        writeResponseGeneric(httpServletResponse, rootNode);


    }

    private JSTreeNode processContainer(Container container)
    {
        JSTreeNode jsTreeNode = new JSTreeNode();
        if (container instanceof Playlist)
        {
            Playlist playlist = (Playlist) container;
            jsTreeNode.setData(playlist.getName());
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", playlist.getId());
            map.put("rel", "playlist");
            jsTreeNode.setAttr(map);
            for (Track track : playlist.getTracks())
            {
                final JSTreeNode trackJSTreeNode = new JSTreeNode();
                trackJSTreeNode.setData(track.getTitle());
                map = new HashMap<String, String>();
                map.put("id", track.getId());
                map.put("rel", "track");
                trackJSTreeNode.setAttr(map);
                jsTreeNode.addChild(trackJSTreeNode);
            }
        }
        else if (container instanceof Folder)
        {
            final Folder folder = (Folder) container;
            jsTreeNode.setData(folder.getName());
            Map<String, String> map = new HashMap<String, String>();
            map = new HashMap<String, String>();
            map.put("id", folder.getName());
            map.put("rel", "folder");
            jsTreeNode.setAttr(map);
            final List<Container> children = folder.getChildren();
            for (Container child : children)
            {
                jsTreeNode.addChild(processContainer(child));
            }
        }
        return jsTreeNode;
    }

}
