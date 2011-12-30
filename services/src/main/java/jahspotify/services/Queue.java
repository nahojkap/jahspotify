package jahspotify.services;

import java.util.concurrent.*;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class Queue
{
    private QueueTrack _currentlyPlaying;
    private BlockingDeque<QueueTrack> _queuedTracks = new LinkedBlockingDeque<QueueTrack>();

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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Queue))
        {
            return false;
        }

        final Queue queue = (Queue) o;

        if (_currentlyPlaying != null ? !_currentlyPlaying.equals(queue._currentlyPlaying) : queue._currentlyPlaying != null)
        {
            return false;
        }
        if (_id != null ? !_id.equals(queue._id) : queue._id != null)
        {
            return false;
        }
        if (_queueConfiguration != null ? !_queueConfiguration.equals(queue._queueConfiguration) : queue._queueConfiguration != null)
        {
            return false;
        }
        if (_queueStatistics != null ? !_queueStatistics.equals(queue._queueStatistics) : queue._queueStatistics != null)
        {
            return false;
        }
        if (_queuedTracks != null ? !_queuedTracks.equals(queue._queuedTracks) : queue._queuedTracks != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _currentlyPlaying != null ? _currentlyPlaying.hashCode() : 0;
        result = 31 * result + (_queuedTracks != null ? _queuedTracks.hashCode() : 0);
        result = 31 * result + (_id != null ? _id.hashCode() : 0);
        result = 31 * result + (_queueStatistics != null ? _queueStatistics.hashCode() : 0);
        result = 31 * result + (_queueConfiguration != null ? _queueConfiguration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Queue{" +
                "_currentlyPlaying=" + _currentlyPlaying +
                ", _queuedTracks=" + _queuedTracks +
                ", _id=" + _id +
                ", _queueStatistics=" + _queueStatistics +
                ", _queueConfiguration=" + _queueConfiguration +
                '}';
    }
}
