package jahspotify.service.echonest;

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

import javax.annotation.PostConstruct;

import com.echonest.api.v4.Song;
import jahspotify.JahSpotify;
import jahspotify.media.*;
import jahspotify.metadata.Metadata;
import jahspotify.metadata.search.*;
import jahspotify.query.*;
import jahspotify.service.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Lazy(value = false)
public class EchoNestSpotifyBridge
{
    private Log _log = LogFactory.getLog(EchoNestSpotifyBridge.class);

    @Autowired
    private EchoNestService _echoNestService;

    @Autowired
    private QueueManager _queueManager;

    @PostConstruct
    private void initialize()
    {
        _queueManager.addQueueListener(new AbstractQueueListener()
        {
            @Override
            public void newTrackAtFront(final Link queue, final QueueTrack queueTrack)
            {
            }

            @Override
            public void queueEmpty(final Link queue, final QueueTrack lastTrackPlayed)
            {
                _log.debug("Queue empty, will retrieve next dynamic track");
                // FIXME: Retrieve next track and add it to the queue
            }
        });
    }

    public Link retrieveNextDynamicTrack(Track track)
    {
        return null;
    }

    private Link retrieveSpotifyLink(Song song)
    {
        Metadata md = new Metadata();
        final TrackSearchResult trackSearchResult = md.searchTracks(AndQuery.and(ArtistQuery.artist(song.getArtistName()), TokenQuery.token(song.getTitle())));

        if (trackSearchResult.getTracks() == null || trackSearchResult.getTracks().isEmpty())
        {
            return null;
        }

        return jahspotify.media.Link.create(trackSearchResult.getTracks().get(0).getHref());

    }

}
