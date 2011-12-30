package jahspotify.services;

/**
 * @author Johan Lindquist
 */
public class QueueStatus
{
    private int _totalTracksPlayed;
    private int _totalTracksCompleted;
    private int _totalTracksSkipped;
    private long _totalPlaytime;
    private int _maxQueueSize;
    private int _currentQueueSize;
    private MediaPlayerState _mediaPlayerState;

    public int getCurrentQueueSize()
    {
        return _currentQueueSize;
    }

    public void setCurrentQueueSize(final int currentQueueSize)
    {
        _currentQueueSize = currentQueueSize;
    }

    public int getMaxQueueSize()
    {
        return _maxQueueSize;
    }

    public void setMaxQueueSize(final int maxQueueSize)
    {
        _maxQueueSize = maxQueueSize;
    }

    public long getTotalPlaytime()
    {
        return _totalPlaytime;
    }

    public void setTotalPlaytime(final long totalPlaytime)
    {
        _totalPlaytime = totalPlaytime;
    }

    public int getTotalTracksCompleted()
    {
        return _totalTracksCompleted;
    }

    public void setTotalTracksCompleted(final int totalTracksCompleted)
    {
        _totalTracksCompleted = totalTracksCompleted;
    }

    public int getTotalTracksPlayed()
    {
        return _totalTracksPlayed;
    }

    public void setTotalTracksPlayed(final int totalTracksPlayed)
    {
        _totalTracksPlayed = totalTracksPlayed;
    }

    public int getTotalTracksSkipped()
    {
        return _totalTracksSkipped;
    }

    public void setTotalTracksSkipped(final int totalTracksSkipped)
    {
        _totalTracksSkipped = totalTracksSkipped;
    }

    public void setMediaPlayerState(final MediaPlayerState mediaPlayerState)
    {
        _mediaPlayerState = mediaPlayerState;
    }

    public MediaPlayerState getMediaPlayerState()
    {
        return _mediaPlayerState;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueStatus))
        {
            return false;
        }

        final QueueStatus that = (QueueStatus) o;

        if (_currentQueueSize != that._currentQueueSize)
        {
            return false;
        }
        if (_maxQueueSize != that._maxQueueSize)
        {
            return false;
        }
        if (_totalPlaytime != that._totalPlaytime)
        {
            return false;
        }
        if (_totalTracksCompleted != that._totalTracksCompleted)
        {
            return false;
        }
        if (_totalTracksPlayed != that._totalTracksPlayed)
        {
            return false;
        }
        if (_totalTracksSkipped != that._totalTracksSkipped)
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
        int result = _totalTracksPlayed;
        result = 31 * result + _totalTracksCompleted;
        result = 31 * result + _totalTracksSkipped;
        result = 31 * result + (int) (_totalPlaytime ^ (_totalPlaytime >>> 32));
        result = 31 * result + _maxQueueSize;
        result = 31 * result + _currentQueueSize;
        result = 31 * result + (_mediaPlayerState != null ? _mediaPlayerState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueStatus{" +
                "_currentQueueSize=" + _currentQueueSize +
                ", _totalTracksPlayed=" + _totalTracksPlayed +
                ", _totalTracksCompleted=" + _totalTracksCompleted +
                ", _totalTracksSkipped=" + _totalTracksSkipped +
                ", _totalPlaytime=" + _totalPlaytime +
                ", _maxQueueSize=" + _maxQueueSize +
                ", _mediaPlayerState=" + _mediaPlayerState +
                '}';
    }
}
