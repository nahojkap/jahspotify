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

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class MediaPlayerStatus
{
    private MediaPlayerState _mediaPlayerState;
    private QueueTrack _currentTrack;
    private int _trackDuration;
    private int _trackCurrentOffset;

    public QueueTrack getCurrentTrack()
    {
        return _currentTrack;
    }

    public void setCurrentTrack(final QueueTrack currentTrack)
    {
        _currentTrack = currentTrack;
    }

    public MediaPlayerState getMediaPlayerState()
    {
        return _mediaPlayerState;
    }

    public void setMediaPlayerState(final MediaPlayerState mediaPlayerState)
    {
        _mediaPlayerState = mediaPlayerState;
    }

    public int getTrackCurrentOffset()
    {
        return _trackCurrentOffset;
    }

    public void setTrackCurrentOffset(final int trackCurrentOffset)
    {
        _trackCurrentOffset = trackCurrentOffset;
    }

    public int getTrackDuration()
    {
        return _trackDuration;
    }

    public void setTrackDuration(final int trackDuration)
    {
        _trackDuration = trackDuration;
    }

    @Override
    public String toString()
    {
        return "MediaPlayerStatus{" +
                "_currentTrack=" + _currentTrack +
                ", _mediaPlayerState=" + _mediaPlayerState +
                ", _trackDuration=" + _trackDuration +
                ", _trackCurrentOffset=" + _trackCurrentOffset +
                '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof MediaPlayerStatus))
        {
            return false;
        }

        final MediaPlayerStatus that = (MediaPlayerStatus) o;

        if (_trackCurrentOffset != that._trackCurrentOffset)
        {
            return false;
        }
        if (_trackDuration != that._trackDuration)
        {
            return false;
        }
        if (_currentTrack != null ? !_currentTrack.equals(that._currentTrack) : that._currentTrack != null)
        {
            return false;
        }
        if (_mediaPlayerState != that._mediaPlayerState)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _mediaPlayerState != null ? _mediaPlayerState.hashCode() : 0;
        result = 31 * result + (_currentTrack != null ? _currentTrack.hashCode() : 0);
        result = 31 * result + _trackDuration;
        result = 31 * result + _trackCurrentOffset;
        return result;
    }
}
