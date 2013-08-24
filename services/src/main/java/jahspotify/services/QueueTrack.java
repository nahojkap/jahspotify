package jahspotify.services;

import java.util.*;

import jahspotify.media.Link;

/** Representation of a queued track within the system.  Contains the necessary information to
 * play the track as well as identifying the track in the queue.  Also adds some meta-data
 * storage for storing random meta-data with the track
 * @author Johan Lindquist
 */
public class QueueTrack
{
    private Link _queueEntry;
    private Link _queue;
    private Link _trackID;
    private String _source;
    private int _length;
    private int _offset = -1;

    private Map<String, String> _metadata = new HashMap<String, String>();

    public QueueTrack(final Link queueEntry, final Link trackUri, final String source)
    {
        _queueEntry = queueEntry;
        _queue = Link.create("jahspotify:queue:" + _queueEntry.getQueue());
        _trackID = trackUri;
        _source = source;
    }

    public Link getQueueEntry()
    {
        return _queueEntry;
    }

    public int getOffset()
    {
        return _offset;
    }

    public void setOffset(final int offset)
    {
        _offset = offset;
    }

    public void setLength(final int length)
    {
        _length = length;
    }

    public int getLength()
    {
        return _length;
    }

    public Link getTrackUri()
    {
        return _trackID;
    }

    public Link getQueue()
    {
        return _queue;
    }

    public Map<String, String> getMetadata()
    {
        return _metadata;
    }

    public String getSource()
    {
        return _source;
    }

    @Override
    public String toString()
    {
        return "QueueTrack{" +
                "_length=" + _length +
                ", _queueEntry=" + _queueEntry +
                ", _queue=" + _queue +
                ", _trackID=" + _trackID +
                ", _source='" + _source + '\'' +
                ", _offset=" + _offset +
                ", _metadata=" + _metadata +
                "} " + super.toString();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueTrack))
        {
            return false;
        }

        final QueueTrack that = (QueueTrack) o;

        if (_length != that._length)
        {
            return false;
        }
        if (_offset != that._offset)
        {
            return false;
        }
        if (_metadata != null ? !_metadata.equals(that._metadata) : that._metadata != null)
        {
            return false;
        }
        if (_queue != null ? !_queue.equals(that._queue) : that._queue != null)
        {
            return false;
        }
        if (_queueEntry != null ? !_queueEntry.equals(that._queueEntry) : that._queueEntry != null)
        {
            return false;
        }
        if (_source != null ? !_source.equals(that._source) : that._source != null)
        {
            return false;
        }
        if (_trackID != null ? !_trackID.equals(that._trackID) : that._trackID != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _queueEntry != null ? _queueEntry.hashCode() : 0;
        result = 31 * result + (_queue != null ? _queue.hashCode() : 0);
        result = 31 * result + (_trackID != null ? _trackID.hashCode() : 0);
        result = 31 * result + (_source != null ? _source.hashCode() : 0);
        result = 31 * result + _length;
        result = 31 * result + _offset;
        result = 31 * result + (_metadata != null ? _metadata.hashCode() : 0);
        return result;
    }
}
