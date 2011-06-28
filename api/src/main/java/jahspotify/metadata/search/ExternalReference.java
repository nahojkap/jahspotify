package jahspotify.metadata.search;

/**
 * @author Johan Lindquist
 */
public class ExternalReference
{
    private String type;
    private String id;

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
        return "ExternalReference{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
