package jahspotify.media;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds information about an artist.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class Artist extends Media
{
    /**
     * Name of this artist.
     */
    private String name;

    /**
     * The identifier for this artists portrait image (40-character string).
     */
    private String portrait;

    /**
     * A {@link List} of genres.
     */
    private List<String> genres;

    /**
     * A {@link List} of years active.
     */
    private List<String> yearsActive;

    /**
     * A {@link List} of biographies.
     */
    private List<Biography> bios;

    /**
     * A {@link List} of albums.
     */
    private List<Album> albums;

    /**
     * A {@link List} of similar artists.
     */
    private List<Artist> similarArtists;

    /**
     * Creates an empty {@link Artist} object.
     */
    public Artist()
    {
        this.name = null;
        this.portrait = null;
        this.genres = new ArrayList<String>();
        this.yearsActive = new ArrayList<String>();
        this.bios = new ArrayList<Biography>();
        this.albums = new ArrayList<Album>();
        this.similarArtists = new ArrayList<Artist>();
    }

    /**
     * Creates an {@link Artist} object with the specified {@code id}.
     *
     * @param id A 32-character hex string or a Spotify URI.
     */
    public Artist(String id)
    {
        this(id, null);
    }

    /**
     * Creates an {@link Artist} object with the specified {@code id} and {@code name}.
     *
     * @param id   A 32-character hex string or a Spotify URI.
     * @param name Name of the artist or {@code null}.
     */
    public Artist(String id, String name)
    {
        super(id);

        /* Set object properties. */
        this.name = name;
        this.portrait = null;
        this.genres = new ArrayList<String>();
        this.yearsActive = new ArrayList<String>();
        this.bios = new ArrayList<Biography>();
        this.similarArtists = new ArrayList<Artist>();
        this.albums = new ArrayList<Album>();
    }

    /**
     * Create a link from this artist.
     *
     * @return A {@link Link} object which can then
     *         be used to retreive the Spotify URI.
     */
    public Link getLink()
    {
        return Link.create(this);
    }

    /**
     * Get the artists name.
     *
     * @return The name of this artist.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the artists name.
     *
     * @param name The desired name of this artist.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the artists portrait image identifier.
     *
     * @return A 40-character hex string.
     */
    public String getPortrait()
    {
        return this.portrait;
    }

    /**
     * Set the artists portrait image identifier.
     *
     * @param portrait A 40-character hex string.
     */
    public void setPortrait(String portrait)
    {
        this.portrait = portrait;
    }

    /**
     * Get genres for this artist.
     *
     * @return A {@link List} of genres.
     */
    public List<String> getGenres()
    {
        return this.genres;
    }

    /**
     * Set genres for this artist.
     *
     * @param genres A {@link List} of genres.
     */
    public void setGenres(List<String> genres)
    {
        this.genres = genres;
    }

    /**
     * Get active years for this artist.
     *
     * @return A {@link List} of active years.
     */
    public List<String> getYearsActive()
    {
        return this.yearsActive;
    }

    /**
     * Set active years for this artist.
     *
     * @param yearsActive A {@link List} of active years.
     */
    public void setYearsActive(List<String> yearsActive)
    {
        this.yearsActive = yearsActive;
    }

    /**
     * Get biographies for this artist.
     *
     * @return A {@link List} of {@link Biography} objects.
     */
    public List<Biography> getBios()
    {
        return this.bios;
    }

    /**
     * Set biographies for this artist.
     *
     * @param bios A {@link List} of {@link Biography} objects.
     */
    public void setBios(List<Biography> bios)
    {
        this.bios = bios;
    }

    /**
     * Get albums for this artist.
     *
     * @return A {@link List} of {@link Album} objects.
     */
    public List<Album> getAlbums()
    {
        return this.albums;
    }

    /**
     * Set albums for this artist.
     *
     * @param albums A {@link List} of {@link Album} objects.
     */
    public void setAlbums(List<Album> albums)
    {
        this.albums = albums;
    }

    /**
     * Get similar artists for this artist.
     *
     * @return A {@link List} of {@link Artist} objects.
     */
    public List<Artist> getSimilarArtists()
    {
        return this.similarArtists;
    }

    /**
     * Set similar artists for this artist.
     *
     * @param similarArtists A {@link List} of {@link Artist} objects.
     */
    public void setSimilarArtists(List<Artist> similarArtists)
    {
        this.similarArtists = similarArtists;
    }

    /**
     * Determines if an object is equal to this {@link Artist} object.
     * If both objects are {@link Artist} objects, it will compare their identifiers.
     *
     * @param o Another object to compare.
     * @return true of the objects are equal, false otherwise.
     */
    public boolean equals(Object o)
    {
        if (o instanceof Artist)
        {
            Artist a = (Artist) o;

            if (this.id.equals(a.id))
            {
                return true;
            }

            for (String id : this.redirects)
            {
                if (id.equals(a.id))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return the hash code of this {@link Artist} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@link Artist} objects hash code.
     */
    public int hashCode()
    {
        return (this.id != null) ? this.id.hashCode() : 0;
    }

    public String toString()
    {
        return String.format("[Artist: %s]", this.name);
    }
}
