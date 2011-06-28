package jahspotify.metadata.search;

/**
 * @author Johan Lindquist
 */
public class MediaResult
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
        return "MediaResult{" +
                "href='" + href + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
