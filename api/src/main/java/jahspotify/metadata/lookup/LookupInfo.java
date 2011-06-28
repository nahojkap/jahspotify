package jahspotify.metadata.lookup;

/**
 * @author Johan Lindquist
 */
public class LookupInfo
{
    private String type;

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
        return "LookupInfo{" +
                "type='" + type + '\'' +
                '}';
    }
}
