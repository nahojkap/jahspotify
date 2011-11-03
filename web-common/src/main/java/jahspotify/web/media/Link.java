package jahspotify.web.media;

/**
 * @author Johan Lindquist
 */
public class Link
{
    private String id;
    private String type;

    public Link()
    {
    }

    public Link(final String id, final String type)
    {
        this.id = id;
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Link{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
