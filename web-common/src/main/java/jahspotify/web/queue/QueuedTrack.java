package jahspotify.web.queue;

/**
 * @author Johan Lindquist
 */
public class QueuedTrack
{
    private String id;
    private String trackID;

    public QueuedTrack(final String id, final String trackID)
    {
        this.id = id;
        this.trackID = trackID;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getTrackID()
    {
        return trackID;
    }

    public void setTrackID(final String trackID)
    {
        this.trackID = trackID;
    }

    @Override
    public String toString()
    {
        return "QueuedTrack{" +
                "id='" + id + '\'' +
                ", trackID='" + trackID + '\'' +
                "} " + super.toString();
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

        if (id != null ? !id.equals(that.id) : that.id != null)
        {
            return false;
        }
        if (trackID != null ? !trackID.equals(that.trackID) : that.trackID != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (trackID != null ? trackID.hashCode() : 0);
        return result;
    }
}
