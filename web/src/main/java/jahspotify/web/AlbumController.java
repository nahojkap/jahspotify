package jahspotify.web;

import java.io.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
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
public class AlbumController
{
    private Log _log = LogFactory.getLog(AlbumController.class);

   /* @RequestMapping(value = "/playlist*//*", method = RequestMethod.GET)
    public void testPlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        Playlist playlist = _jahSpotify.readPlaylist(uri);
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

    @RequestMapping(value = "/track*//*", method = RequestMethod.GET)
    public void testTrack(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        Track track = _jahSpotify.readTrack(uri);
        _log.debug("Got track: " + track);
        Gson gson = new Gson();
        try
        {
            final PrintWriter writer = httpServletResponse.getWriter();
            writer.write(gson.toJson(track));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }*/

}
