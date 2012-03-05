package jahspotify.storage.statistics;

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

import jahspotify.media.Link;

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

    public TrackHistory(final Link queue, final Link trackLink, final String source, final String id, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime)
    {
        this.queue = queue;
        this.trackLink = trackLink;
        this.id = id;
        this.completeTrackPlayed = completeTrackPlayed;
        this.secondsPlayed = secondsPlayed;
        this.startTime = startTime;
        this.source = source;
    }

    public String getId()
    {
        return id;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(final String source)
    {
        this.source = source;
    }

    public Link getQueue()
    {
        return queue;
    }

    public void setQueue(final Link queue)
    {
        this.queue = queue;
    }

    public boolean isCompleteTrackPlayed()
    {
        return completeTrackPlayed;
    }

    public void setCompleteTrackPlayed(final boolean completeTrackPlayed)
    {
        this.completeTrackPlayed = completeTrackPlayed;
    }

    public int getSecondsPlayed()
    {
        return secondsPlayed;
    }

    public void setSecondsPlayed(final int secondsPlayed)
    {
        this.secondsPlayed = secondsPlayed;
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

    @Override
    public String toString()
    {
        return "TrackHistory{" +
                "completeTrackPlayed=" + completeTrackPlayed +
                ", trackLink=" + trackLink +
                ", id='" + id + '\'' +
                ", secondsPlayed=" + secondsPlayed +
                ", startTime=" + startTime +
                ", queue=" + queue +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof TrackHistory))
        {
            return false;
        }

        final TrackHistory that = (TrackHistory) o;

        if (completeTrackPlayed != that.completeTrackPlayed)
        {
            return false;
        }
        if (secondsPlayed != that.secondsPlayed)
        {
            return false;
        }
        if (startTime != that.startTime)
        {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null)
        {
            return false;
        }
        if (queue != null ? !queue.equals(that.queue) : that.queue != null)
        {
            return false;
        }
        if (source != null ? !source.equals(that.source) : that.source != null)
        {
            return false;
        }
        if (trackLink != null ? !trackLink.equals(that.trackLink) : that.trackLink != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = trackLink != null ? trackLink.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (completeTrackPlayed ? 1 : 0);
        result = 31 * result + secondsPlayed;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (queue != null ? queue.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}
