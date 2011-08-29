package jahspotify;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class SearchResult
{
   private String _query;
   private String _didYouMean;
   private List<Link> _tracksFound;
   private int _totalNumTracks;
   private int _trackOffset;
   private List<Link> _albumsFound;
   private int _totalNumAlbums;
   private int _albumOffset;
   private List<Link> _artistsFound;
   private int _totalNumArtists;
   private int _artistOffset;

    public String getDidYouMean()
    {
        return _didYouMean;
    }

    public int getAlbumOffset()
    {
        return _albumOffset;
    }

    public List<Link> getAlbumsFound()
    {
        return _albumsFound;
    }

    public int getArtistOffset()
    {
        return _artistOffset;
    }

    public List<Link> getArtistsFound()
    {
        return _artistsFound;
    }

    public String getQuery()
    {
        return _query;
    }

    public int getTotalNumAlbums()
    {
        return _totalNumAlbums;
    }

    public int getTotalNumArtists()
    {
        return _totalNumArtists;
    }

    public int getTotalNumTracks()
    {
        return _totalNumTracks;
    }

    public int getTrackOffset()
    {
        return _trackOffset;
    }

    public List<Link> getTracksFound()
    {
        return _tracksFound;
    }

    @Override
    public String toString()
    {
        return "SearchResult{" +
                "_albumOffset=" + _albumOffset +
                ", _query='" + _query + '\'' +
                ", _didYouMean='" + _didYouMean + '\'' +
                ", _tracksFound=" + _tracksFound +
                ", _totalNumTracks=" + _totalNumTracks +
                ", _trackOffset=" + _trackOffset +
                ", _albumsFound=" + _albumsFound +
                ", _totalNumAlbums=" + _totalNumAlbums +
                ", _artistsFound=" + _artistsFound +
                ", _totalNumArtists=" + _totalNumArtists +
                ", _artistOffset=" + _artistOffset +
                '}';
    }
}
