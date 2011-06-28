package jahspotify.metadata.search;

import java.util.*;

/**
 * @author Johan Lindquist
 */
public class AlbumResult extends MediaResult
{
    private float popularity;
    private String released;
    private List<ExternalReference> externalIds;
    private List<MediaResult> artists;
    private Availability availability;

    public Availability getAvailability()
    {
        return availability;
    }

    public void setAvailability(final Availability availability)
    {
        this.availability = availability;
    }

    public List<ExternalReference> getExternalIds()
    {
        return externalIds;
    }

    public void setExternalIds(final List<ExternalReference> externalIds)
    {
        this.externalIds = externalIds;
    }

    public float getPopularity()
    {
        return popularity;
    }

    public void setPopularity(final float popularity)
    {
        this.popularity = popularity;
    }

    public List<MediaResult> getArtists()
    {
        return artists;
    }

    public void setArtists(final List<MediaResult> artists)
    {
        this.artists = artists;
    }

    public String getReleased()
    {
        return released;
    }

    public void setReleased(final String released)
    {
        this.released = released;
    }

    @Override
    public String toString()
    {
        return "AlbumResult{" +
                "artists=" + artists +
                ", popularity=" + popularity +
                ", released='" + released + '\'' +
                ", externalIds=" + externalIds +
                ", availability=" + availability +
                "} " + super.toString();
    }
}
