package jahspotify.web;

import javax.servlet.http.*;

import jahspotify.services.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class MediaPlayerController extends BaseController
{
    @Autowired
    private MediaPlayer _mediaPlayer;

    @RequestMapping(value = "/player/pause", method = RequestMethod.GET)
    public void pause(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.pause();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_PAUSED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/resume", method = RequestMethod.GET)
    public void resume(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.play();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_RESUMED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/play", method = RequestMethod.GET)
    public void play(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.play();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_STARTED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/skip", method = RequestMethod.GET)
    public void skip(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.skip();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        simpleStatusResponse.setDetail("TRACK_SKIPPED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

}
