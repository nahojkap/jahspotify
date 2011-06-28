package jahspotify.metadata.lookup;

import java.util.List;

import jahspotify.metadata.search.*;

/**
 * @author Johan Lindquist
 */
public class AlbumMediaLookup extends MediaLookup
{
    private String artistId;
    private int released;
    private String artist;
    private List<ExternalReference> externalIds;
    private Availability availability;

    public Availability getAvailability()
    {
        return availability;
    }

    public void setAvailability(final Availability availability)
    {
        this.availability = availability;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(final String artist)
    {
        this.artist = artist;
    }

    public String getArtistId()
    {
        return artistId;
    }

    public void setArtistId(final String artistId)
    {
        this.artistId = artistId;
    }

    public List<ExternalReference> getExternalIds()
    {
        return externalIds;
    }

    public void setExternalIds(final List<ExternalReference> externalIds)
    {
        this.externalIds = externalIds;
    }

    public int getReleased()
    {
        return released;
    }

    public void setReleased(final int released)
    {
        this.released = released;
    }

    @Override
    public String toString()
    {
        return "AlbumMediaLookup{" +
                "artist='" + artist + '\'' +
                ", artistId='" + artistId + '\'' +
                ", released=" + released +
                ", externalIds=" + externalIds +
                ", availability=" + availability +
                "} " + super.toString();
    }
}
