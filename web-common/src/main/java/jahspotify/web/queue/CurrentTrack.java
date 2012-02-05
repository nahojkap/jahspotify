package jahspotify.web.queue;

/**
 * @author Johan Lindquist
 */
public class CurrentTrack
{
    private String id;
    private String trackID;
    private int length;
    private int offset;

    public CurrentTrack(final String id, final String trackID)
    {
        this.id = id;
        this.trackID = trackID;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(final int length)
    {
        this.length = length;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(final int offset)
    {
        this.offset = offset;
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
