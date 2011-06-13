package jahspotify.media;

import java.util.ArrayList;
import java.util.List;

import jahspotify.util.Hex;

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

    private List<Link> tracks;

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

    public Playlist(String id)
    {
        this(id, null, null, false);
    }

    public Playlist(String id, String name, String author, boolean collaborative)
    {
        /* Check if id is a 32-character hex string. */
        if (id.length() == 32 && Hex.isHex(id))
        {
            this.id = id;
        }
        /* Otherwise try to parse it as a Spotify URI. */
        else
        {
            try
            {
                this.id = Link.create(id).getId();
            }
            catch (Link.InvalidSpotifyURIException e)
            {
                throw new IllegalArgumentException(
                        "Given id is neither a 32-character" +
                                "hex string nor a valid Spotify URI.", e
                );
            }
        }

        /* Set other playlist properties. */
        this.name = name;
        this.author = author;
        this.tracks = new ArrayList<Link>();
        this.collaborative = collaborative;
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

    public void addTrack(Link track)
    {
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

    public String getPicture()
    {
        return this.picture;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    /**
     * Create a link from this playlist.
     *
     * @return A {@link Link} object which can then
     *         be used to retreive the Spotify URI.
     */
    public Link getLink()
    {
        return Link.create(this);
    }

    public static Playlist fromResult(String name, String author, Result result)
    {
        Playlist playlist = new Playlist();

        playlist.name = name;
        playlist.author = author;

        for (Link track : result.getTracks())
        {
            playlist.tracks.add(track);
        }

        return playlist;
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

    public String toString()
    {
        return String.format("[Playlist: %s, %s]", this.author, this.name);
    }
}
