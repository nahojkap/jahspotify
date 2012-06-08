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

import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;

import jahspotify.services.*;
import jahspotify.services.Queue;
import jahspotify.services.QueueStatus;
import jahspotify.web.queue.*;
import jahspotify.web.queue.QueueConfiguration;

/**
 * @author Johan Lindquist
 */
public class QueueWebHelper
{

    public static QueueState convertToQueueStatus(final jahspotify.services.MediaPlayerState mediaPlayerState)
    {
        switch (mediaPlayerState)
        {
            case PAUSED:
                return QueueState.PAUSED;
            case PLAYING:
                return QueueState.PLAYING;
            case STOPPED:
                return QueueState.STOPPED;
            default:
                throw new IllegalStateException("Unhandled media player state: " + mediaPlayerState);
        }
    }


    public static QueueConfiguration convertToWebQueueConfiguration(final jahspotify.services.QueueConfiguration queueConfiguration)
    {
        jahspotify.web.queue.QueueConfiguration currentQueueConfiguration = new jahspotify.web.queue.QueueConfiguration();
        currentQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        currentQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        currentQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
        currentQueueConfiguration.setAutoRefill(queueConfiguration.isAutoRefill());
        currentQueueConfiguration.setReportEmptyQueue(queueConfiguration.isReportEmptyQueue());
        currentQueueConfiguration.setReportTrackChanges(queueConfiguration.isReportTrackChanges());
        currentQueueConfiguration.setCallbackURL(queueConfiguration.getCallbackURL() == null ? null : queueConfiguration.getCallbackURL().toString());
        return currentQueueConfiguration;
    }

    public static jahspotify.services.QueueConfiguration mergeConfigurations(final QueueConfiguration webQueueConfiguration, final jahspotify.services.QueueConfiguration currentQueueConfiguration) throws Exception
    {
        final jahspotify.services.QueueConfiguration queueConfiguration = new jahspotify.services.QueueConfiguration();
        queueConfiguration.setRepeatCurrentQueue(webQueueConfiguration.isRepeatCurrentQueue());
        queueConfiguration.setRepeatCurrentTrack(webQueueConfiguration.isRepeatCurrentTrack());
        queueConfiguration.setShuffle(webQueueConfiguration.isShuffle());
        queueConfiguration.setAutoRefill(webQueueConfiguration.isAutoRefill());
        queueConfiguration.setReportEmptyQueue(webQueueConfiguration.isReportEmptyQueue());
        queueConfiguration.setReportTrackChanges(webQueueConfiguration.isReportTrackChanges());
        queueConfiguration.setCallbackURL(webQueueConfiguration.getCallbackURL() == null ? null : new URL(webQueueConfiguration.getCallbackURL()));
        return queueConfiguration;
    }

    public static jahspotify.web.queue.QueueStatus convertToWebQueueStatus(final QueueStatus queueStatus)
    {
        jahspotify.web.queue.QueueStatus webQueueStatus = new jahspotify.web.queue.QueueStatus();

        webQueueStatus.setQueueState(QueueWebHelper.convertToQueueStatus(queueStatus.getMediaPlayerState()));
        webQueueStatus.setCurrentQueueSize(queueStatus.getCurrentQueueSize());
        webQueueStatus.setMaxQueueSize(queueStatus.getMaxQueueSize());
        webQueueStatus.setQueueState(QueueState.valueOf(queueStatus.getMediaPlayerState().name()));
        webQueueStatus.setTotalPlaytime(queueStatus.getTotalPlaytime());
        webQueueStatus.setTotalTracksCompleted(queueStatus.getTotalTracksCompleted());
        webQueueStatus.setTotalTracksPlayed(queueStatus.getTotalTracksPlayed());
        webQueueStatus.setTotalTracksSkipped(queueStatus.getTotalTracksSkipped());

        return webQueueStatus;
    }

    public static List<QueuedTrack> convertToWebQueuedTracks(final BlockingDeque<QueueTrack> queuedTracks)
    {
        if (queuedTracks.isEmpty())
        {
            return Collections.emptyList();
        }
        final List<QueuedTrack> queuedWebTracks = new ArrayList<QueuedTrack>();
        for (QueueTrack queuedTrack : queuedTracks)
        {
            queuedWebTracks.add(new QueuedTrack(queuedTrack.getId(), queuedTrack.getTrackUri().asString()));
        }

        return queuedWebTracks;
    }

    public static CurrentQueue convertToWebQueue(final Queue queue, final jahspotify.services.QueueStatus queueStatus)
    {
        final jahspotify.web.queue.CurrentQueue currentCurrentQueue = new jahspotify.web.queue.CurrentQueue();
        currentCurrentQueue.setQueueStatus(QueueWebHelper.convertToWebQueueStatus(queueStatus));
        currentCurrentQueue.setId(queue.getId().getId());
        currentCurrentQueue.setQueueConfiguration(QueueWebHelper.convertToWebQueueConfiguration(queue.getQueueConfiguration()));

        final QueueTrack currentlyPlaying = queue.getCurrentlyPlaying();
        if (currentlyPlaying != null)
        {
            final CurrentTrack webCurrentlyPlaying = new CurrentTrack(currentlyPlaying.getId(), currentlyPlaying.getTrackUri().asString());
            webCurrentlyPlaying.setLength(currentlyPlaying.getLength());
            webCurrentlyPlaying.setOffset(currentlyPlaying.getOffset());
            currentCurrentQueue.setCurrentlyPlaying(webCurrentlyPlaying);
        }
        currentCurrentQueue.setQueuedTracks(QueueWebHelper.convertToWebQueuedTracks(queue.getQueuedTracks()));
        return currentCurrentQueue;
    }
}
