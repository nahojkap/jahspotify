package jahspotify.media;

import jahspotify.media.Link.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a playlist.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
 */
public class Playlist extends Container
{
    private Link id;
    private String name;
    private String author;
    private boolean collaborative;

    private int index;

    private String description;
    private Link picture;

    private List<Link> tracks;
    private int numTracks;
    private int trackIndex;

    public Playlist()
    {
        this.id = null;
        this.name = null;
        this.author = null;
        this.tracks = new ArrayList<Link>();
        this.collaborative = false;
        this.description = null;
        this.picture = null;
    }

    public Link getId()
    {
        return this.id;
    }

    public void setId(Link id)
    {
        this.id = id;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(final int index)
    {
        this.index = index;
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

    public void addTrack(Link track)
    {
    	if (track.getType() == Type.LOCAL)
    	{
    		// Don't add local tracks, can't do anything with them...
    		return;
    	}
        if (tracks == null)
        {
            tracks = new ArrayList<Link>();
        }
        this.tracks.add(track);
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public List<Link> getTracks()
    {
        return this.tracks;
    }

    public void setTracks(List<Link> tracks)
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

    public Link getPicture()
    {
        return this.picture;
    }

    public void setPicture(Link picture)
    {
        this.picture = picture;
    }

    public int getNumTracks()
    {
        return numTracks;
    }

    public void setNumTracks(final int numTracks)
    {
        this.numTracks = numTracks;
    }

    public int getTrackIndex()
    {
        return trackIndex;
    }

    public void setTrackIndex(final int trackIndex)
    {
        this.trackIndex = trackIndex;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Playlist))
        {
            return false;
        }

        final Playlist playlist = (Playlist) o;

        if (collaborative != playlist.collaborative)
        {
            return false;
        }
        if (index != playlist.index)
        {
            return false;
        }
        if (numTracks != playlist.numTracks)
        {
            return false;
        }
        if (trackIndex != playlist.trackIndex)
        {
            return false;
        }
        if (author != null ? !author.equals(playlist.author) : playlist.author != null)
        {
            return false;
        }
        if (description != null ? !description.equals(playlist.description) : playlist.description != null)
        {
            return false;
        }
        if (id != null ? !id.equals(playlist.id) : playlist.id != null)
        {
            return false;
        }
        if (name != null ? !name.equals(playlist.name) : playlist.name != null)
        {
            return false;
        }
        if (picture != null ? !picture.equals(playlist.picture) : playlist.picture != null)
        {
            return false;
        }
        if (tracks != null ? !tracks.equals(playlist.tracks) : playlist.tracks != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (collaborative ? 1 : 0);
        result = 31 * result + index;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (tracks != null ? tracks.hashCode() : 0);
        result = 31 * result + numTracks;
        result = 31 * result + trackIndex;
        return result;
    }

    @Override
    public String toString()
    {
        return "Playlist{" +
                "author='" + author + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", collaborative=" + collaborative +
                ", index=" + index +
                ", description='" + description + '\'' +
                ", picture=" + picture +
                ", tracks=" + tracks +
                ", numTracks=" + numTracks +
                ", trackIndex=" + trackIndex +
                "} " + super.toString();
    }
}
