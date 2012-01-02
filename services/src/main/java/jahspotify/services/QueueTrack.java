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
    private Link _queue;
    private String _id;
    private Link _trackID;
    private Map<String, String> _metadata = new HashMap<String, String>();
    public static final String QUEUED_TRACK_SOURCE = "QUEUED_TRACK_SOURCE";

    public QueueTrack(final String id, final Link trackUri, final Link queue)
    {
        _id = id;
        _trackID = trackUri;
        _queue = queue;
    }

    public String getId()
    {
        return _id;
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

        if (_id != null ? !_id.equals(that._id) : that._id != null)
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
        if (_trackID != null ? !_trackID.equals(that._trackID) : that._trackID != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _queue != null ? _queue.hashCode() : 0;
        result = 31 * result + (_id != null ? _id.hashCode() : 0);
        result = 31 * result + (_trackID != null ? _trackID.hashCode() : 0);
        result = 31 * result + (_metadata != null ? _metadata.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueTrack{" +
                "_id='" + _id + '\'' +
                ", _queue=" + _queue +
                ", _trackID=" + _trackID +
                ", _metadata=" + _metadata +
                '}';
    }
}
