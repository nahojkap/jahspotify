package jahspotify.metadata.lookup;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class ArtistMediaLookup extends MediaLookup
{
    private List<LookupResult> albums;

    public List<LookupResult> getAlbums()
    {
        return albums;
    }

    public void setAlbums(final List<LookupResult> albums)
    {
        this.albums = albums;
    }

    @Override
    public String toString()
    {
        return "ArtistMediaLookup{" +
                "albums=" + albums +
                "} " + super.toString();
    }
}
