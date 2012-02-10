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
    private Link _trackLink;
    private String _id;
    private boolean _completeTrackPlayed;
    private int _secondsPlayed;
    private long _startTime;
    private Link _queue;
    private String _source;

    public TrackHistory(final Link queue, final Link trackLink, final String source, final String id, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime)
    {
        _queue = queue;
        _trackLink = trackLink;
        _id = id;
        _completeTrackPlayed = completeTrackPlayed;
        _secondsPlayed = secondsPlayed;
        _startTime = startTime;
        _source = source;
    }

    public String getId()
    {
        return _id;
    }

    public String getSource()
    {
        return _source;
    }

    public void setSource(final String source)
    {
        _source = source;
    }

    public Link getQueue()
    {
        return _queue;
    }

    public void setQueue(final Link queue)
    {
        _queue = queue;
    }

    public boolean isCompleteTrackPlayed()
    {
        return _completeTrackPlayed;
    }

    public void setCompleteTrackPlayed(final boolean completeTrackPlayed)
    {
        _completeTrackPlayed = completeTrackPlayed;
    }

    public int getSecondsPlayed()
    {
        return _secondsPlayed;
    }

    public void setSecondsPlayed(final int secondsPlayed)
    {
        _secondsPlayed = secondsPlayed;
    }

    public long getStartTime()
    {
        return _startTime;
    }

    public void setStartTime(final long startTime)
    {
        _startTime = startTime;
    }

    public Link getTrackLink()
    {
        return _trackLink;
    }

    public void setTrackLink(final Link trackLink)
    {
        _trackLink = trackLink;
    }

    @Override
    public String toString()
    {
        return "TrackHistory{" +
                "_completeTrackPlayed=" + _completeTrackPlayed +
                ", _trackLink=" + _trackLink +
                ", _id='" + _id + '\'' +
                ", _secondsPlayed=" + _secondsPlayed +
                ", _startTime=" + _startTime +
                ", _queue=" + _queue +
                ", _source='" + _source + '\'' +
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

        if (_completeTrackPlayed != that._completeTrackPlayed)
        {
            return false;
        }
        if (_secondsPlayed != that._secondsPlayed)
        {
            return false;
        }
        if (_startTime != that._startTime)
        {
            return false;
        }
        if (_id != null ? !_id.equals(that._id) : that._id != null)
        {
            return false;
        }
        if (_queue != null ? !_queue.equals(that._queue) : that._queue != null)
        {
            return false;
        }
        if (_source != null ? !_source.equals(that._source) : that._source != null)
        {
            return false;
        }
        if (_trackLink != null ? !_trackLink.equals(that._trackLink) : that._trackLink != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _trackLink != null ? _trackLink.hashCode() : 0;
        result = 31 * result + (_id != null ? _id.hashCode() : 0);
        result = 31 * result + (_completeTrackPlayed ? 1 : 0);
        result = 31 * result + _secondsPlayed;
        result = 31 * result + (int) (_startTime ^ (_startTime >>> 32));
        result = 31 * result + (_queue != null ? _queue.hashCode() : 0);
        result = 31 * result + (_source != null ? _source.hashCode() : 0);
        return result;
    }
}
