package jahspotify.metadata.search;

/**
 * @author Johan Lindquist
 */
public class ArtistResult extends MediaResult
{
    private float popularity;

    public float getPopularity()
    {
        return popularity;
    }

    public void setPopularity(final float popularity)
    {
        this.popularity = popularity;
    }

    @Override
    public String toString()
    {
        return "ArtistResult{" +
                "popularity=" + popularity +
                "} " + super.toString();
    }
}
