package jahspotify.web.media;

import java.util.*;


/**
 * Holds information about an artist.
 *
 * @author Felix Bruns <felixbruns@web.de>
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
    private List<String> portraits;

    /**
     * A {@String java.util.List} of genres.
     */
    private List<String> genres;

    /**
     * A {@String java.util.List} of years active.
     */
    private List<String> yearsActive;

    /**
     * A {@String java.util.List} of biographies.
     */
    private String bios;

    /**
     * A {@String java.util.List} of albums.
     */
    private List<String> albums;

     /**
     * A {@String java.util.List} of tracks.
     */
    private List<String> tracks;

    /**
     * A {@String java.util.List} of similar artists.
     */
    private List<String> similarArtists;

    /**
     * Creates an empty {@String Artist} object.
     */
    public Artist()
    {
        this.name = null;
        this.portraits = new ArrayList<String>();
        this.genres = new ArrayList<String>();
        this.yearsActive = new ArrayList<String>();
        this.bios = null;
        this.albums = new ArrayList<String>();
        this.similarArtists = new ArrayList<String>();
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
     * @return A list of image Strings
     */
    public List<String> getPortraits()
    {
        return this.portraits;
    }

    /**
     * Set the artists portrait image identifier.
     *
     * @param portraits A list of image Strings
     */
    public void setPortraits(List<String> portraits)
    {
        this.portraits = portraits;
    }

    /**
     * Adds a artist portrait image identifier.
     *
     * @param portrait An image Strings
     */
    public void addPortrait(String portrait)
    {
        if (this.portraits == null)
        {
            this.portraits = new ArrayList<String>();
        }
        portraits.add(portrait);
    }

    /**
     * Get genres for this artist.
     *
     * @return A {@String java.util.List} of genres.
     */
    public List<String> getGenres()
    {
        return this.genres;
    }

    /**
     * Set genres for this artist.
     *
     * @param genres A {@String java.util.List} of genres.
     */
    public void setGenres(List<String> genres)
    {
        this.genres = genres;
    }

    /**
     * Get active years for this artist.
     *
     * @return A {@String java.util.List} of active years.
     */
    public List<String> getYearsActive()
    {
        return this.yearsActive;
    }

    /**
     * Set active years for this artist.
     *
     * @param yearsActive A {@String java.util.List} of active years.
     */
    public void setYearsActive(List<String> yearsActive)
    {
        this.yearsActive = yearsActive;
    }

    /**
     * Get biographies for this artist.
     *
     * @return A {@String java.util.List} of {@String Biography} objects.
     */
    public String getBios()
    {
        return this.bios;
    }

    /**
     * Set biographies for this artist.
     *
     * @param bios A {@String java.util.List} of {@String Biography} objects.
     */
    public void setBios(String bios)
    {
        this.bios = bios;
    }

    /**
     * Get albums for this artist.
     *
     * @return A {@String java.util.List} of {@String Album} objects.
     */
    public List<String> getAlbums()
    {
        return this.albums;
    }

    /**
     * Set albums for this artist.
     *
     * @param albums A {@String java.util.List} of {@String Album} objects.
     */
    public void setAlbums(List<String> albums)
    {
        this.albums = albums;
    }


    public void addAlbum(String album)
    {
        if (albums == null)
        {
            albums = new ArrayList<String>();
        }
        albums.add(album);
    }

    /**
     * Get similar artists for this artist.
     *
     * @return A {@String java.util.List} of {@String Artist} objects.
     */
    public List<String> getSimilarArtists()
    {
        return this.similarArtists;
    }

    /**
     * Set similar artists for this artist.
     *
     * @param similarArtists A {@String java.util.List} of {@String Artist} objects.
     */
    public void setSimilarArtists(List<String> similarArtists)
    {
        this.similarArtists = similarArtists;
    }

    public void addSimilarArtist(String similarArtist)
    {
        if (similarArtists == null)
        {
            similarArtists = new ArrayList<String>();
        }
        similarArtists.add(similarArtist);
    }

    /**
     * Determines if an object is equal to this {@String Artist} object.
     * If both objects are {@String Artist} objects, it will compare their identifiers.
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
     * Return the hash code of this {@String Artist} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@String Artist} objects hash code.
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
