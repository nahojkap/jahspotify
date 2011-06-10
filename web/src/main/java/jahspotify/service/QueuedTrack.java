package jahspotify.service;

/**
 * @author Johan Lindquist
 */
public class QueuedTrack
{
    String _id;
    String _trackID;

    public QueuedTrack(final String id, final String trackID)
    {
        _id = id;
        _trackID = trackID;
    }

    public String getId()
    {
        return _id;
    }

    public void setId(final String id)
    {
        _id = id;
    }

    public String getTrackID()
    {
        return _trackID;
    }

    public void setTrackID(final String trackID)
    {
        _trackID = trackID;
    }

    @Override
    public String toString()
    {
        return "QueuedTrack{" +
                "_id='" + _id + '\'' +
                ", _trackID='" + _trackID + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueuedTrack))
        {
            return false;
        }

        final QueuedTrack that = (QueuedTrack) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null)
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
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_trackID != null ? _trackID.hashCode() : 0);
        return result;
    }
}
