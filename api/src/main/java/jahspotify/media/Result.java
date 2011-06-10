package jahspotify.media;

import java.util.ArrayList;
import java.util.List;

public class Result
{
    private String query;
    private String suggestion;
    private int totalArtists;
    private int totalAlbums;
    private int totalTracks;
    private List<Artist> artists;
    private List<Album> albums;
    private List<Track> tracks;

    public Result()
    {
        this.query = null;
        this.suggestion = null;
        this.totalArtists = 0;
        this.totalAlbums = 0;
        this.totalTracks = 0;
        this.artists = new ArrayList<Artist>();
        this.albums = new ArrayList<Album>();
        this.tracks = new ArrayList<Track>();
    }

    /**
     * Create a link from this search result.
     *
     * @return A {@link Link} object which can then
     *         be used to retreive the Spotify URI.
     */
    public Link getLink()
    {
        return Link.create(this);
    }

    public String getQuery()
    {
        return this.query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getSuggestion()
    {
        return this.suggestion;
    }

    public void setSuggestion(String suggestion)
    {
        this.suggestion = suggestion;
    }

    public int getTotalArtists()
    {
        return this.totalArtists;
    }

    public void setTotalArtists(int totalArtists)
    {
        this.totalArtists = totalArtists;
    }

    public int getTotalAlbums()
    {
        return this.totalAlbums;
    }

    public void setTotalAlbums(int totalAlbums)
    {
        this.totalAlbums = totalAlbums;
    }

    public int getTotalTracks()
    {
        return this.totalTracks;
    }

    public void setTotalTracks(int totalTracks)
    {
        this.totalTracks = totalTracks;
    }

    public List<Artist> getArtists()
    {
        return this.artists;
    }

    public void setArtists(List<Artist> artists)
    {
        this.artists = artists;
    }

    public void addArtist(Artist artist)
    {
        this.artists.add(artist);
    }

    public List<Album> getAlbums()
    {
        return this.albums;
    }

    public void setAlbums(List<Album> albums)
    {
        this.albums = albums;
    }

    public void addAlbum(Album album)
    {
        this.albums.add(album);
    }

    public List<Track> getTracks()
    {
        return this.tracks;
    }

    public void setTracks(List<Track> tracks)
    {
        this.tracks = tracks;
    }

    public void addTrack(Track track)
    {
        this.tracks.add(track);
    }

    public boolean equals(Object o)
    {
        if (o instanceof Result)
        {
            return this.query.equals(((Result) o).query);
        }

        return false;
    }

    public int hashCode()
    {
        return (this.query != null) ? this.query.hashCode() : 0;
    }
}
