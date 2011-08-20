package jahspotify;

/**
 * @author Johan Lindquist
 */
public class Search
{
    int trackOffset = 0;
    int numTracks = 255;

    int albumOffset = 0;
    int numAlbums = 255;

    int artistOffset = 0;
    int numArtists = 255;

    Query _query;

    public Search(final Query query)
    {
        _query = query;
    }

    public Query getQuery()
    {
        return _query;
    }

    public void setQuery(final Query query)
    {
        _query = query;
    }

    public int getAlbumOffset()
    {
        return albumOffset;
    }

    public void setAlbumOffset(final int albumOffset)
    {
        this.albumOffset = albumOffset;
    }

    public int getArtistOffset()
    {
        return artistOffset;
    }

    public void setArtistOffset(final int artistOffset)
    {
        this.artistOffset = artistOffset;
    }

    public int getNumAlbums()
    {
        return numAlbums;
    }

    public void setNumAlbums(final int numAlbums)
    {
        this.numAlbums = numAlbums;
    }

    public int getNumArtists()
    {
        return numArtists;
    }

    public void setNumArtists(final int numArtists)
    {
        this.numArtists = numArtists;
    }

    public int getNumTracks()
    {
        return numTracks;
    }

    public void setNumTracks(final int numTracks)
    {
        this.numTracks = numTracks;
    }

    public int getTrackOffset()
    {
        return trackOffset;
    }

    public void setTrackOffset(final int trackOffset)
    {
        this.trackOffset = trackOffset;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Search))
        {
            return false;
        }

        final Search search = (Search) o;

        if (albumOffset != search.albumOffset)
        {
            return false;
        }
        if (artistOffset != search.artistOffset)
        {
            return false;
        }
        if (numAlbums != search.numAlbums)
        {
            return false;
        }
        if (numArtists != search.numArtists)
        {
            return false;
        }
        if (numTracks != search.numTracks)
        {
            return false;
        }
        if (trackOffset != search.trackOffset)
        {
            return false;
        }
        if (_query != null ? !_query.equals(search._query) : search._query != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = trackOffset;
        result = 31 * result + numTracks;
        result = 31 * result + albumOffset;
        result = 31 * result + numAlbums;
        result = 31 * result + artistOffset;
        result = 31 * result + numArtists;
        result = 31 * result + (_query != null ? _query.hashCode() : 0);
        return result;
    }
}