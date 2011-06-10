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
public class PlaylistController
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
        Gson gson = new Gson();
        try
        {
            final PrintWriter writer = httpServletResponse.getWriter();
            writer.write(gson.toJson(playlist));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/playlist-tree/", method = RequestMethod.GET)
    public void testPlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            String result = "[{ \"data\" : \"A node\", \"children\" : [ { \"data\" : \"Only child\", \"state\" : \"closed\" } ], \"state\" : \"open\" },\"Ajax node\"]";
            httpServletResponse.setContentType("application/json");
            final PrintWriter writer = httpServletResponse.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    @RequestMapping(value = "/library/", method = RequestMethod.GET)
    public void retrieveLibrary(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Library library = _jahSpotifyService.getJahSpotify().retrieveLibrary();

            JSTreeNode rootNode = new JSTreeNode();
            rootNode.setData("ROOT");

            final List<Container> children = library.getChildren();
            for (Container child : children)
            {
                rootNode.addChild(processContainer(child));
            }


            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            final PrintWriter writer = httpServletResponse.getWriter();
            writer.write(gson.toJson(rootNode));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private JSTreeNode processContainer(Container container)
    {
        JSTreeNode jsTreeNode = new JSTreeNode();
        if (container instanceof Playlist)
        {
            Playlist playlist = (Playlist) container;
            jsTreeNode.setData(playlist.getName());
            // "attr" : { "id" : "node_identificator"
            Map<String,String> map = new HashMap<String, String>();
            map.put("id",playlist.getId());
            map.put("rel","playlist");
            jsTreeNode.setAttr(map);
            for (Track track : playlist.getTracks())
            {
                final JSTreeNode trackJSTreeNode = new JSTreeNode();
                trackJSTreeNode.setData(track.getTitle());
                map = new HashMap<String, String>();
                map.put("id",track.getId());
                map.put("rel", "track");
                trackJSTreeNode.setAttr(map);
                trackJSTreeNode.setType("track");
                jsTreeNode.addChild(trackJSTreeNode);
            }
        }
        else if (container instanceof Folder)
        {
            final Folder folder = (Folder) container;
            jsTreeNode.setData(folder.getName());
            Map<String,String> map = new HashMap<String, String>();
            map = new HashMap<String, String>();
            map.put("id",folder.getName());
            map.put("rel","folder");
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
