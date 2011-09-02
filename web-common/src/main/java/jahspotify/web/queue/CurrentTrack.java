package jahspotify.web.queue;

/**
 * @author Johan Lindquist
 */
public class CurrentTrack
{
    private String id;
    private String trackID;

    public CurrentTrack(final String id, final String trackID)
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

    @Override
    public String toString()
    {
        return "CurrentTrack{" +
                "id='" + id + '\'' +
                ", trackID='" + trackID + '\'' +
                '}';
    }
}
