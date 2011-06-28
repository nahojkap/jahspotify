package jahspotify.metadata.search;

import java.util.*;

/**
 * @author Johan Lindquist
 */
public class TrackResult extends MediaResult
{
    private AlbumResult album;
    private float popularity;
    private List<ExternalReference> externalIds;
    private List<MediaResult> artists;
    private Availability availability;
    private float length;
    private int trackNumber;

    public int getTrackNumber()
    {
        return trackNumber;
    }

    public void setTrackNumber(final int trackNumber)
    {
        this.trackNumber = trackNumber;
    }

    public AlbumResult getAlbum()
    {
        return album;
    }

    public void setAlbum(final AlbumResult album)
    {
        this.album = album;
    }

    public float getLength()
    {
        return length;
    }

    public void setLength(final float length)
    {
        this.length = length;
    }

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

    @Override
    public String toString()
    {
        return "TrackResult{" +
                "album=" + album +
                ", popularity=" + popularity +
                ", externalIds=" + externalIds +
                ", artists=" + artists +
                ", availability=" + availability +
                ", length=" + length +
                ", trackNumber=" + trackNumber +
                "} " + super.toString();
    }
}
