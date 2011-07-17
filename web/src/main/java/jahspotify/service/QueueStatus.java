package jahspotify.service;

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
}
