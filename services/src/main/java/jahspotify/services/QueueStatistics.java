package jahspotify.services;

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

/**
 * @author Johan Lindquist
 */
public class QueueStatistics
{
    private int _maxQueueSize;
    private int _numTracksPlayed;
    private int _numTracksSkipped;
    private int _numTracksCompleted;
    private int _totalPlayTime;

    private long _currentTrackStart;

    public long getCurrentTrackStart()
    {
        return _currentTrackStart;
    }

    public void setCurrentTrackStart(final long currentTrackStart)
    {
        _currentTrackStart = currentTrackStart;
    }

    public int getMaxQueueSize()
    {
        return _maxQueueSize;
    }

    public void setMaxQueueSize(final int maxQueueSize)
    {
        _maxQueueSize = maxQueueSize;
    }

    public void incrementTracksCompleted()
    {
        _numTracksCompleted += 1;
    }

    public int getNumTracksCompleted()
    {
        return _numTracksCompleted;
    }

    public void setNumTracksCompleted(final int numTracksCompleted)
    {
        _numTracksCompleted = numTracksCompleted;
    }

    public void incrementTracksPlayed()
    {
        _numTracksPlayed += 1;
    }

    public int getNumTracksPlayed()
    {
        return _numTracksPlayed;
    }

    public void setNumTracksPlayed(final int numTracksPlayed)
    {
        _numTracksPlayed = numTracksPlayed;
    }

    public void incrementTracksSkipped()
    {
        _numTracksSkipped += 1;
    }

    public int getNumTracksSkipped()
    {
        return _numTracksSkipped;
    }

    public void setNumTracksSkipped(final int numTracksSkipped)
    {
        _numTracksSkipped = numTracksSkipped;
    }

    public void incrementTotalPlayTime(long additionalTime)
    {
        _totalPlayTime += additionalTime;
    }

    public int getTotalPlayTime()
    {
        return _totalPlayTime;
    }

    public void setTotalPlayTime(final int totalPlayTime)
    {
        _totalPlayTime = totalPlayTime;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueStatistics))
        {
            return false;
        }

        final QueueStatistics that = (QueueStatistics) o;

        if (_currentTrackStart != that._currentTrackStart)
        {
            return false;
        }
        if (_maxQueueSize != that._maxQueueSize)
        {
            return false;
        }
        if (_numTracksCompleted != that._numTracksCompleted)
        {
            return false;
        }
        if (_numTracksPlayed != that._numTracksPlayed)
        {
            return false;
        }
        if (_numTracksSkipped != that._numTracksSkipped)
        {
            return false;
        }
        if (_totalPlayTime != that._totalPlayTime)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _maxQueueSize;
        result = 31 * result + _numTracksPlayed;
        result = 31 * result + _numTracksSkipped;
        result = 31 * result + _numTracksCompleted;
        result = 31 * result + _totalPlayTime;
        result = 31 * result + (int) (_currentTrackStart ^ (_currentTrackStart >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueStatistics{" +
                "_currentTrackStart=" + _currentTrackStart +
                ", _maxQueueSize=" + _maxQueueSize +
                ", _numTracksPlayed=" + _numTracksPlayed +
                ", _numTracksSkipped=" + _numTracksSkipped +
                ", _numTracksCompleted=" + _numTracksCompleted +
                ", _totalPlayTime=" + _totalPlayTime +
                '}';
    }

    public void updateMaxQueueSize(final int size)
    {
        if (size > _maxQueueSize)
        {
            _maxQueueSize = size;
        }

    }
}
