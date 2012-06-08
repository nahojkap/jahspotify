package jahspotify.web.ui;

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

import java.util.*;

import jahspotify.services.*;
import jahspotify.services.Queue;
import jahspotify.web.api.BaseController;
import jahspotify.web.media.FullTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping (value = "/ui/queue")
public class QueueBrowserUIController extends BaseController
{
    @Autowired
    QueueManager _queueManager;

    @RequestMapping (value = "/current")
    public ModelAndView retrieveCurrentQueue()
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/current-queue.jsp");

        final Queue currentQueue = _queueManager.getCurrentQueue(0);

        final QueueTrack currentlyPlaying = currentQueue.getCurrentlyPlaying();

        if (currentlyPlaying != null)
        {
            FullTrack fullTrack = createFullTrack(_jahSpotify.readTrack(currentlyPlaying.getTrackUri()));
            modelAndView.addObject("current-track",fullTrack);
        }

        final List<FullTrack> queuedTracks = new ArrayList<FullTrack>();
        for (QueueTrack queueTrack : currentQueue.getQueuedTracks())
        {
            queuedTracks.add(createFullTrack(_jahSpotify.readTrack(queueTrack.getTrackUri())));
        }

        modelAndView.addObject("queuedTracks",queuedTracks);

        return modelAndView;


    }

}
