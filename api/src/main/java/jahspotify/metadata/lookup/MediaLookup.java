package jahspotify.metadata.lookup;

/**
 * @author Johan Lindquist
 */
public class MediaLookup
{
    private String name;
    private String href;

    public String getHref()
    {
        return href;
    }

    public void setHref(final String href)
    {
        this.href = href;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "LookupResult{" +
                "href='" + href + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
