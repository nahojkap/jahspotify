package jahspotify.services.echonest;

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
import javax.annotation.PostConstruct;

import com.echonest.api.v4.*;
import com.echonest.api.v4.Playlist;
import jahspotify.JahSpotify;
import jahspotify.media.*;
import jahspotify.media.Artist;
import jahspotify.media.Track;
import jahspotify.metadata.Metadata;
import jahspotify.metadata.search.*;
import jahspotify.query.*;
import jahspotify.services.*;
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

    @Autowired
    private JahSpotifyService _jahSpotifyService;
    private JahSpotify _jahSpotify;

    public static final String ECHO_NEST_SOURCE_ID = "EchoNest";
    public static final String ECHONEST_SESSION_ID = "ECHONEST_SESSION_ID";

    @PostConstruct
    private void initialize()
    {

        _jahSpotify = _jahSpotifyService.getJahSpotify();

        _queueManager.addQueueListener(new AbstractQueueListener()
        {
            @Override
            public void newTrackAtFront(final Link queue, final QueueTrack queueTrack)
            {
            }

            @Override
            public void queueEmpty(final Link queue, final QueueTrack lastTrackPlayed)
            {
                if (!_queueManager.getQueueConfiguration(queue).isAutoRefill())
                {
                    _log.debug("Queue " + queue + " is not configured to auto-refill, skipping");
                    return;
                }

                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(3000);
                            _log.debug("Queue empty, will retrieve next dynamic track");
                            // FIXME: Retrieve next track and add it to the queue
                            if (ECHO_NEST_SOURCE_ID.equals(lastTrackPlayed.getSource()))
                            {
                                // Have already a EchoNest sourced track - use the session id for getting the next one!
                                Playlist echoNestPlaylist = retrieveNextDynamicTrack(lastTrackPlayed.getMetadata().get(ECHONEST_SESSION_ID));
                                Link spotLink = retrieveSpotifyLink(echoNestPlaylist.getSongs().get(0));
                                if (spotLink != null)
                                {
                                    final QueueTrack queueTrack = new QueueTrack(UUID.randomUUID().toString(), spotLink, queue, ECHO_NEST_SOURCE_ID);
                                    queueTrack.getMetadata().put(ECHONEST_SESSION_ID, echoNestPlaylist.getSession());
                                    _queueManager.addToQueue(QueueManager.DEFAULT_QUEUE_LINK, queueTrack);
                                }
                                else
                                {
                                    _log.error("Could not retrieve Spotify link for song: " + echoNestPlaylist.getSongs().get(0));
                                }
                            }
                            else
                            {
                                // Based on the last track, create a new dynamic playlist, adding that to the queue when done
                                Track track = _jahSpotify.readTrack(lastTrackPlayed.getTrackUri());
                                Playlist echoNestPlaylist = retrieveFirstDynamicTrack(track);
                                Link spotLink = retrieveSpotifyLink(echoNestPlaylist.getSongs().get(0));
                                if (spotLink != null)
                                {
                                    final QueueTrack queueTrack = new QueueTrack(UUID.randomUUID().toString(), spotLink, queue,ECHO_NEST_SOURCE_ID);
                                    queueTrack.getMetadata().put(ECHONEST_SESSION_ID, echoNestPlaylist.getSession());
                                    _queueManager.addToQueue(QueueManager.DEFAULT_QUEUE_LINK, queueTrack);
                                }
                                else
                                {
                                    _log.error("Could not retrieve Spotify link for song: " + echoNestPlaylist.getSongs().get(0));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();

            }
        });
    }

    private Playlist retrieveFirstDynamicTrack(final Track track)
    {
        try
        {
            final Set<Link> trackArtists = track.getArtists();
            final List<String> artists = new ArrayList<String>();
            for (final Link trackArtist : trackArtists)
            {
                final Artist artist = _jahSpotify.readArtist(trackArtist);
                if (artist == null)
                {
                    // bad news - means the artist is not loaded yet ... should wait ...
                }
                else
                {
                    artists.add(artist.getName());
                }
            }
            return _echoNestService.retrieveDynamicPlaylist(artists, null, null);
        }
        catch (EchoNestException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Playlist retrieveNextDynamicTrack(String sessionId)
    {
        try
        {
            return _echoNestService.updateDynamicPlaylist(sessionId);
        }
        catch (EchoNestException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
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
