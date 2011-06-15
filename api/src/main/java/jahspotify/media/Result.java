package jahspotify.media;

import java.util.*;

public class Result
{
    private String query;
    private String suggestion;
    private int totalArtists;
    private int totalAlbums;
    private int totalTracks;
    private List<Link> artists;
    private List<Link> albums;
    private List<Link> tracks;

    public Result()
    {
        this.query = null;
        this.suggestion = null;
        this.totalArtists = 0;
        this.totalAlbums = 0;
        this.totalTracks = 0;
        this.artists = new ArrayList<Link>();
        this.albums = new ArrayList<Link>();
        this.tracks = new ArrayList<Link>();
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

    public List<Link> getArtists()
    {
        return this.artists;
    }

    public void setArtists(List<Link> artists)
    {
        this.artists = artists;
    }

    public void addArtist(Link artist)
    {
        this.artists.add(artist);
    }

    public List<Link> getAlbums()
    {
        return this.albums;
    }

    public void setAlbums(List<Link> albums)
    {
        this.albums = albums;
    }

    public void addAlbum(Link album)
    {
        this.albums.add(album);
    }

    public List<Link> getTracks()
    {
        return this.tracks;
    }

    public void setTracks(List<Link> tracks)
    {
        this.tracks = tracks;
    }

    public void addTrack(Link track)
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
