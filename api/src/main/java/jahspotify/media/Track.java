package jahspotify.media;

import java.util.*;


/**
 * Holds information about a track.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
 */
public class Track extends Media
{
    /**
     * Title of this track.
     */
    private String title;

    /**
     * {@link Artist}s of this track.
     */
    private List<Link> artists = new ArrayList<Link>();

    /**
     * A {@link Link} to the album which this track belongs to.
     */
    private Link album;

    /**
     * Track number on a certain disk.
     */
    private int trackNumber;

    /**
     * Length of this track in seconds.
     */
    private int length;

    /**
     * The identifier for this tracks cover image (32-character string).
     */
    private String cover;

    /**
     * If this track is explicit.
     */
    private boolean explicit;

    /**
     * Creates an empty {@link Track} object.
     */
    public Track()
    {
        this.title = null;
        this.artists = new ArrayList<Link>();
        this.album = null;
        this.trackNumber = -1;
        this.length = -1;
        this.cover = null;
    }

    /**
     * Get the tracks title.
     *
     * @return The title of this track.
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Set the tracks title.
     *
     * @param title The desired name of this track.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Get the tracks artists.
     *
     * @return An {@link Artist}s object.
     */
    public List<Link> getArtists()
    {
        return this.artists;
    }

    /**
     * Set the tracks artists.
     *
     * @param artists The desired {@link Artist}s of this track.
     */
    public void setArtists(List<Link> artists)
    {
        this.artists = artists;
    }

    public void addArtist(Link artist)
    {
        if (artists == null)
        {
            artists = new ArrayList<Link>();
        }
        artists.add(artist);

    }

    /**
     * Get the tracks album.
     *
     * @return An {@link Album} object.
     */
    public Link getAlbum()
    {
        return this.album;
    }

    /**
     * Set the tracks album.
     *
     * @param album The desired {@link Album} of this track.
     */
    public void setAlbum(Link album)
    {
        this.album = album;
    }

    /**
     * Get the tracks number on a certain disk.
     *
     * @return An integer denoting the track number or -1 if not available.
     */
    public int getTrackNumber()
    {
        return this.trackNumber;
    }

    /**
     * Set the tracks number on a certain disk.
     *
     * @param trackNumber A positive integer greater than zero.
     */
    public void setTrackNumber(int trackNumber)
    {
        /* Check if track number is valid. */
        if (trackNumber <= 0)
        {
            throw new IllegalArgumentException("Expecting a track number greater than zero.");
        }

        this.trackNumber = trackNumber;
    }

    /**
     * Get the tracks length in milliseconds.
     *
     * @return An integer representing the length in milliseconds or -1 if not available.
     */
    public int getLength()
    {
        return this.length;
    }

    /**
     * Set the tracks length in milliseconds.
     */
    public void setLength(int length)
    {
        /* Check if length is valid. */
        if (length <= 0)
        {
            throw new IllegalArgumentException("Expecting a length greater than zero.");
        }

        this.length = length;
    }

    /**
     * Get the tracks cover image identifier.
     *
     * @return A 32-character image identifier.
     */
    public String getCover()
    {
        return this.cover;
    }

    /**
     * Set the tracks cover image identifier.
     *
     * @param cover A 32-character image identifier.
     */
    public void setCover(String cover)
    {
        this.cover = cover;
    }

    /**
     * Return if this track is explicit.
     *
     * @return A boolean value.
     */
    public boolean isExplicit()
    {
        return this.explicit;
    }

    /**
     * Set if this track is explicit.
     *
     * @param explicit A boolean value.
     */
    public void setExplicit(boolean explicit)
    {
        this.explicit = explicit;
    }


    /**
     * Determines if an object is equal to this {@link Track} object.
     * If both objects are {@link Track} objects, it will compare their identifiers.
     *
     * @param o Another object to compare.
     * @return true of the objects are equal, false otherwise.
     */
    public boolean equals(Object o)
    {
        if (o instanceof Track)
        {
            Track t = (Track) o;

            if (this.id.equals(t.id))
            {
                return true;
            }

            for (String id : this.redirects)
            {
                if (id.equals(t.id))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return the hash code of this {@link Track} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@link Track} objects hash code.
     */
    public int hashCode()
    {
        return (this.id != null) ? this.id.hashCode() : 0;
    }

    public String toString()
    {
        return String.format("[Track: %s, %s, %s]", this.artists, this.album, this.title);
    }
}
