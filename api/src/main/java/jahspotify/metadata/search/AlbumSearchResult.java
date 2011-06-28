package jahspotify.metadata.search;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class AlbumSearchResult extends SearchResult
{
    private List<AlbumResult> albums;

    public List<AlbumResult> getAlbums()
    {
        return albums;
    }

    public void setAlbums(final List<AlbumResult> albums)
    {
        this.albums = albums;
    }

    @Override
    public String toString()
    {
        return "AlbumSearchResult{" +
                "albums=" + albums +
                "} " + super.toString();
    }
}
