package jahspotify.media;

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
    private List<Link> portraits;

    /**
     * A {@link List} of genres.
     */
    private Set<String> genres;

    /**
     * A {@link List} of years active.
     */
    private Set<String> yearsActive;

    /**
     * A {@link List} of biographies.
     */
    private String bios;

    /**
     * A {@link List} of albums.
     */
    private Set<Link> albums;

     /**
     * A {@link List} of tracks.
     */
    private Set<Link> topHitTracks;

    /**
     * A {@link List} of similar artists.
     */
    private Set<Link> similarArtists;

    /**
     * Creates an empty {@link Artist} object.
     */
    public Artist()
    {
        this.name = null;
        this.portraits = new ArrayList<Link>();
        this.genres = new HashSet<String>();
        this.yearsActive = new HashSet<String>();
        this.bios = null;
        this.albums = new HashSet<Link>();
        this.similarArtists = new HashSet<Link>();
        this.topHitTracks = new HashSet<Link>();
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
     * @return A list of image links
     */
    public List<Link> getPortraits()
    {
        return this.portraits;
    }

    /**
     * Set the artists portrait image identifier.
     *
     * @param portraits A list of image links
     */
    public void setPortraits(List<Link> portraits)
    {
        this.portraits = portraits;
    }

    /**
     * Adds a artist portrait image identifier.
     *
     * @param portrait An image links
     */
    public void addPortrait(Link portrait)
    {
        if (this.portraits == null)
        {
            this.portraits = new ArrayList<Link>();
        }
        portraits.add(portrait);
    }

    /**
     * Get genres for this artist.
     *
     * @return A {@link List} of genres.
     */
    public Set<String> getGenres()
    {
        return this.genres;
    }

    /**
     * Set genres for this artist.
     *
     * @param genres A {@link List} of genres.
     */
    public void setGenres(Set<String> genres)
    {
        this.genres = genres;
    }

    /**
     * Get active years for this artist.
     *
     * @return A {@link List} of active years.
     */
    public Set<String> getYearsActive()
    {
        return this.yearsActive;
    }

    /**
     * Set active years for this artist.
     *
     * @param yearsActive A {@link List} of active years.
     */
    public void setYearsActive(Set<String> yearsActive)
    {
        this.yearsActive = yearsActive;
    }

    /**
     * Get biographies for this artist.
     *
     * @return A {@link List} of {@link Biography} objects.
     */
    public String getBios()
    {
        return this.bios;
    }

    /**
     * Set biographies for this artist.
     *
     * @param bios A {@link List} of {@link Biography} objects.
     */
    public void setBios(String bios)
    {
        this.bios = bios;
    }

    /**
     * Get albums for this artist.
     *
     * @return A {@link List} of {@link Album} objects.
     */
    public Set<Link> getAlbums()
    {
        return this.albums;
    }

    /**
     * Set albums for this artist.
     *
     * @param albums A {@link List} of {@link Album} objects.
     */
    public void setAlbums(Set<Link> albums)
    {
        this.albums = albums;
    }


    public void addAlbum(Link album)
    {
        if (albums == null)
        {
            albums = new HashSet<Link>();
        }
        albums.add(album);
    }

    /**
     * Get similar artists for this artist.
     *
     * @return A {@link List} of {@link Artist} objects.
     */
    public Set<Link> getSimilarArtists()
    {
        return this.similarArtists;
    }

    /**
     * Set similar artists for this artist.
     *
     * @param similarArtists A {@link List} of {@link Artist} objects.
     */
    public void setSimilarArtists(Set<Link> similarArtists)
    {
        this.similarArtists = similarArtists;
    }

    public void addSimilarArtist(Link similarArtist)
    {
        if (similarArtists == null)
        {
            similarArtists = new HashSet<Link>();
        }
        similarArtists.add(similarArtist);
    }

    public Set<Link> getTopHitTracks()
    {
        return topHitTracks;
    }

    public void setTopHitTracks(final Set<Link> topHitTracks)
    {
        this.topHitTracks = topHitTracks;
    }

    public void addTopHitTrack(final Link topHitTrack)
    {
        if (topHitTracks == null)
        {
            topHitTracks = new HashSet<Link>();
        }
        topHitTracks.add(topHitTrack);
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
