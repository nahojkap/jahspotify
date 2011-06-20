package jahspotify.service;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private QueueTrack _currentlyPlaying;
    private List<QueueTrack> _queuedTracks;

    private boolean _shuffle;
    private boolean _repeatCurrentQueue;
    private boolean _repeatCurrentTrack;

    public CurrentQueue(final QueueTrack currentlyPlaying, final List<QueueTrack> queuedTracks)
    {
        _currentlyPlaying = currentlyPlaying;
        _queuedTracks = queuedTracks;
    }

    public QueueTrack getCurrentlyPlaying()
    {
        return _currentlyPlaying;
    }

    public void setCurrentlyPlaying(final QueueTrack currentlyPlaying)
    {
        _currentlyPlaying = currentlyPlaying;
    }

    public List<QueueTrack> getQueuedTracks()
    {
        return _queuedTracks;
    }

    public void setQueuedTracks(final List<QueueTrack> queuedTracks)
    {
        _queuedTracks = queuedTracks;
    }

    public boolean isRepeatCurrentQueue()
    {
        return _repeatCurrentQueue;
    }

    public void setRepeatCurrentQueue(final boolean repeatCurrentQueue)
    {
        _repeatCurrentQueue = repeatCurrentQueue;
    }

    public boolean isRepeatCurrentTrack()
    {
        return _repeatCurrentTrack;
    }

    public void setRepeatCurrentTrack(final boolean repeatCurrentTrack)
    {
        _repeatCurrentTrack = repeatCurrentTrack;
    }

    public boolean isShuffle()
    {
        return _shuffle;
    }

    public void setShuffle(final boolean shuffle)
    {
        _shuffle = shuffle;
    }
}
