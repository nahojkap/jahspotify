package jahspotify.web;

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
}
