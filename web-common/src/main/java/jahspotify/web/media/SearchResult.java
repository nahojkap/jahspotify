package jahspotify.web.media;

import java.util.*;

public class SearchResult
{
    private String query;
    private String suggestion;
    private int totalArtists;
    private int totalAlbums;
    private int totalTracks;
    private List<String> artists;
    private List<String> albums;
    private List<String> tracks;

    public SearchResult()
    {
        this.query = null;
        this.suggestion = null;
        this.totalArtists = 0;
        this.totalAlbums = 0;
        this.totalTracks = 0;
        this.artists = new ArrayList<String>();
        this.albums = new ArrayList<String>();
        this.tracks = new ArrayList<String>();
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

    public List<String> getArtists()
    {
        return this.artists;
    }

    public void setArtists(List<String> artists)
    {
        this.artists = artists;
    }

    public void addArtist(String artist)
    {
        this.artists.add(artist);
    }

    public List<String> getAlbums()
    {
        return this.albums;
    }

    public void setAlbums(List<String> albums)
    {
        this.albums = albums;
    }

    public void addAlbum(String album)
    {
        this.albums.add(album);
    }

    public List<String> getTracks()
    {
        return this.tracks;
    }

    public void setTracks(List<String> tracks)
    {
        this.tracks = tracks;
    }

    public void addTrack(String track)
    {
        this.tracks.add(track);
    }

    public boolean equals(Object o)
    {
        if (o instanceof SearchResult)
        {
            return this.query.equals(((SearchResult) o).query);
        }

        return false;
    }

    public int hashCode()
    {
        return (this.query != null) ? this.query.hashCode() : 0;
    }
}
