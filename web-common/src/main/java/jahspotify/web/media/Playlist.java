package jahspotify.web.media;

import java.util.*;

/**
 * Holds information about a playlist.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
 */
public class Playlist extends Container
{
    private String id;
    private String name;
    private String author;
    private boolean collaborative;

    private String description;
    private String picture;

    private List<String> tracks;

    public Playlist()
    {
        this.id = null;
        this.name = null;
        this.author = null;
        this.tracks = new ArrayList<String>();
        this.collaborative = false;
        this.description = null;
        this.picture = null;
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public void addTrack(String track)
    {
        if (tracks == null)
        {
            tracks = new ArrayList<String>();
        }
        this.tracks.add(track);
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public List<String> getTracks()
    {
        return this.tracks;
    }

    public void setTracks(List<String> tracks)
    {
        this.tracks = tracks;
    }

    public boolean hasTracks()
    {
        return !this.tracks.isEmpty();
    }

    public boolean isCollaborative()
    {
        return this.collaborative;
    }

    public void setCollaborative(boolean collaborative)
    {
        this.collaborative = collaborative;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPicture()
    {
        return this.picture;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public boolean equals(Object o)
    {
        if (o instanceof Playlist)
        {
            Playlist p = (Playlist) o;

            return this.id.equals(p.id);
        }

        return false;
    }

    public int hashCode()
    {
        return (this.id != null) ? this.id.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "Playlist{" +
                "author='" + author + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", collaborative=" + collaborative +
                ", description='" + description + '\'' +
                ", picture=" + picture +
                ", tracks=" + tracks +
                "} " + super.toString();
    }
}
