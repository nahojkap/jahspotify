package jahspotify.web.api;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import javax.servlet.http.*;

import jahspotify.services.*;
import jahspotify.services.MediaPlayerStatus;
import jahspotify.web.*;
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
    @Autowired
    private QueueManager _queueManager;

    @RequestMapping(value = "/player/status", method = RequestMethod.GET)
    public void status(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final MediaPlayerStatus mediaPlayerStatus = _mediaPlayer.getMediaPlayerStatus();

        final jahspotify.web.MediaPlayerStatus webMediaPlayerStatus = new jahspotify.web.MediaPlayerStatus();
        webMediaPlayerStatus.setLength(mediaPlayerStatus.getTrackDuration());
        webMediaPlayerStatus.setOffset(mediaPlayerStatus.getTrackCurrentOffset());
        webMediaPlayerStatus.setCurrentGain(_mediaPlayer.getCurrentGain());
        if (mediaPlayerStatus.getCurrentTrack() != null)
        {
            webMediaPlayerStatus.setCurrentTrack(toWebLink(mediaPlayerStatus.getCurrentTrack().getTrackUri()));
        }

        super.writeResponseGeneric(httpServletResponse,webMediaPlayerStatus);


    }

    @RequestMapping(value = "/player/stop", method = RequestMethod.GET)
    public void stop(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.stop();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_STOPPED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/seek", method = RequestMethod.GET)
    public void seek(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        int offset = (httpServletRequest.getParameter("offset") == null ? 0 : Integer.parseInt(httpServletRequest.getParameter("offset")));
        _mediaPlayer.seek(offset);
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("SEEK_COMPLETED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/volume-up", method = RequestMethod.GET)
    public void increaseGain(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        float currentGain = _mediaPlayer.getCurrentGain();
        if (currentGain + 0.05f < 1.0f)
        {
            currentGain += 0.05f;
            _mediaPlayer.setCurrentGain(currentGain);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("VOLUME_UP_COMPLETED");
            writeResponse(httpServletResponse, simpleStatusResponse);
        }
        else
        {
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("MAX_VOLUME_REACHED");
            writeResponse(httpServletResponse, simpleStatusResponse);
        }
    }

    @RequestMapping(value = "/player/volume-down", method = RequestMethod.GET)
    public void decreaseGain(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        float currentGain = _mediaPlayer.getCurrentGain();
        if (currentGain - 0.05f > 0.0f)
        {
            currentGain -= 0.05f;
            _mediaPlayer.setCurrentGain(currentGain);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("VOLUME_DOWN_COMPLETED");
            writeResponse(httpServletResponse, simpleStatusResponse);
        }
        else
        {
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("MIN_VOLUME_REACHED");
            writeResponse(httpServletResponse, simpleStatusResponse);
        }
    }

    public void getCurrentGain(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {

    }

    public void setCurrentGain(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {

    }

    @RequestMapping(value = "/player/pause", method = RequestMethod.GET)
    public void pause(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.pause();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_PAUSED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/resume", method = RequestMethod.GET)
    public void resume(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.play();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_RESUMED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/play", method = RequestMethod.GET)
    public void play(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.play();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("PLAY_STARTED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    @RequestMapping(value = "/player/skip", method = RequestMethod.GET)
    public void skip(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _mediaPlayer.skip();
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("TRACK_SKIPPED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

}
