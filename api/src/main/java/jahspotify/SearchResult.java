package jahspotify;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class SearchResult
{
   private String query;
   private String didYouMean;
   private List<Link> tracksFound;
   private int totalNumTracks;
   private int trackOffset;
   private List<Link> albumsFound;
   private int totalNumAlbums;
   private int albumOffset;
   private List<Link> artistsFound;
   private int totalNumArtists;
   private int artistOffset;

    public int getAlbumOffset()
    {
        return albumOffset;
    }

    public void setAlbumOffset(final int albumOffset)
    {
        this.albumOffset = albumOffset;
    }

    public List<Link> getAlbumsFound()
    {
        return albumsFound;
    }

    public void setAlbumsFound(final List<Link> albumsFound)
    {
        this.albumsFound = albumsFound;
    }

    public int getArtistOffset()
    {
        return artistOffset;
    }

    public void setArtistOffset(final int artistOffset)
    {
        this.artistOffset = artistOffset;
    }

    public List<Link> getArtistsFound()
    {
        return artistsFound;
    }

    public void setArtistsFound(final List<Link> artistsFound)
    {
        this.artistsFound = artistsFound;
    }

    public String getDidYouMean()
    {
        return didYouMean;
    }

    public void setDidYouMean(final String didYouMean)
    {
        this.didYouMean = didYouMean;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }

    public int getTotalNumAlbums()
    {
        return totalNumAlbums;
    }

    public void setTotalNumAlbums(final int totalNumAlbums)
    {
        this.totalNumAlbums = totalNumAlbums;
    }

    public int getTotalNumArtists()
    {
        return totalNumArtists;
    }

    public void setTotalNumArtists(final int totalNumArtists)
    {
        this.totalNumArtists = totalNumArtists;
    }

    public int getTotalNumTracks()
    {
        return totalNumTracks;
    }

    public void setTotalNumTracks(final int totalNumTracks)
    {
        this.totalNumTracks = totalNumTracks;
    }

    public int getTrackOffset()
    {
        return trackOffset;
    }

    public void setTrackOffset(final int trackOffset)
    {
        this.trackOffset = trackOffset;
    }

    public List<Link> getTracksFound()
    {
        return tracksFound;
    }

    public void setTracksFound(final List<Link> tracksFound)
    {
        this.tracksFound = tracksFound;
    }

    @Override
    public String toString()
    {
        return "SearchResult{" +
                "albumOffset=" + albumOffset +
                ", query='" + query + '\'' +
                ", didYouMean='" + didYouMean + '\'' +
                ", tracksFound=" + tracksFound +
                ", totalNumTracks=" + totalNumTracks +
                ", trackOffset=" + trackOffset +
                ", albumsFound=" + albumsFound +
                ", totalNumAlbums=" + totalNumAlbums +
                ", artistsFound=" + artistsFound +
                ", totalNumArtists=" + totalNumArtists +
                ", artistOffset=" + artistOffset +
                '}';
    }
}
