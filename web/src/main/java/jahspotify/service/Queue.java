package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class Queue
{
    private QueueTrack _currentlyPlaying;
    private BlockingDeque<QueueTrack> _queuedTracks = new LinkedBlockingDeque<QueueTrack>();

    private boolean _shuffle;
    private boolean _repeatCurrentQueue;
    private boolean _repeatCurrentTrack;

    private Link _id;

    private QueueStatistics _queueStatistics;
    private QueueConfiguration _queueConfiguration;

    public QueueStatistics getQueueStatistics()
    {
        return _queueStatistics;
    }

    public void setQueueStatistics(final QueueStatistics queueStatistics)
    {
        _queueStatistics = queueStatistics;
    }

    public QueueTrack getCurrentlyPlaying()
    {
        return _currentlyPlaying;
    }

    public void setCurrentlyPlaying(final QueueTrack currentlyPlaying)
    {
        _currentlyPlaying = currentlyPlaying;
    }

    public BlockingDeque<QueueTrack> getQueuedTracks()
    {
        return _queuedTracks;
    }

    public void setQueuedTracks(final BlockingDeque<QueueTrack> queuedTracks)
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

    public Link getId()
    {
        return _id;
    }

    public void setId(final Link id)
    {
        _id = id;
    }

    public void clearCurrentlyPlaying()
    {
        _currentlyPlaying = null;
    }

    public QueueConfiguration getQueueConfiguration()
    {
        return _queueConfiguration;
    }

    public void setQueueConfiguration(final QueueConfiguration queueConfiguration)
    {
        _queueConfiguration = queueConfiguration;
    }
}
