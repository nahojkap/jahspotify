package jahspotify.web.media;

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

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public class TrackHistory
{
    private Link trackLink;
    private String id;
    private boolean completeTrackPlayed;
    private int secondsPlayed;
    private long startTime;
    private Link queue;
    private String source;

    public boolean isCompleteTrackPlayed()
    {
        return completeTrackPlayed;
    }

    public void setCompleteTrackPlayed(final boolean completeTrackPlayed)
    {
        this.completeTrackPlayed = completeTrackPlayed;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public Link getQueue()
    {
        return queue;
    }

    public void setQueue(final Link queue)
    {
        this.queue = queue;
    }

    public int getSecondsPlayed()
    {
        return secondsPlayed;
    }

    public void setSecondsPlayed(final int secondsPlayed)
    {
        this.secondsPlayed = secondsPlayed;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(final String source)
    {
        this.source = source;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(final long startTime)
    {
        this.startTime = startTime;
    }

    public Link getTrackLink()
    {
        return trackLink;
    }

    public void setTrackLink(final Link trackLink)
    {
        this.trackLink = trackLink;
    }
}
