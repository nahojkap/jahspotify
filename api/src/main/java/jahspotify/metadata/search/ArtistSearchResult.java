package jahspotify.metadata.search;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class ArtistSearchResult extends SearchResult
{
    private List<ArtistResult> artists;

    public List<ArtistResult> getArtists()
    {
        return artists;
    }

    public void setArtists(final List<ArtistResult> artists)
    {
        this.artists = artists;
    }

    @Override
    public String toString()
    {
        return "ArtistSearchResult{" +
                "artists=" + artists +
                "} " + super.toString();
    }
}
