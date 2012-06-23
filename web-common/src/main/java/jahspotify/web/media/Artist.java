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
    private List<Link> portraits;

    /**
     * A {@String java.util.List} of genres.
     */
    private List<String> genres;

    /**
     * A {@String java.util.List} of years active.
     */
    private List<String> yearsActive;

    /**
     * A {@String java.util.List} of biography paragraphs.
     */
    private List<String> bioParagraphs;

    /**
     * A {@String java.util.List} of albums.
     */
    private List<Link> albums;

     /**
     * A {@String java.util.List} of tracks.
     */
    private List<Link> topHitTracks;

    /**
     * A {@String java.util.List} of similar artists.
     */
    private List<Link> similarArtists;

    /**
     * Creates an empty {@String Artist} object.
     */
    public Artist()
    {
        this.name = null;
        this.portraits = null;
        this.genres = null;
        this.yearsActive = null;
        this.bioParagraphs = null;
        this.albums = null;
        this.similarArtists = null;
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
    public List<Link> getPortraits()
    {
        return this.portraits;
    }

    /**
     * Set the artists portrait image identifier.
     *
     * @param portraits A list of image Strings
     */
    public void setPortraits(List<Link> portraits)
    {
        this.portraits = portraits;
    }

    /**
     * Adds a artist portrait image identifier.
     *
     * @param portrait An image Strings
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
     * @return A {@String java.util.List} of {@String} objects.
     */
    public List<String> getBioParagraphs()
    {
        return this.bioParagraphs;
    }

    /**
     * Set biographies for this artist.
     *
     * @param bioParagraphs A {@String java.util.List} of {@String} objects.
     */
    public void setBioParagraphs(List<String> bioParagraphs)
    {
        this.bioParagraphs = bioParagraphs;
    }

    /**
     * Get albums for this artist.
     *
     * @return A {@String java.util.List} of {@String Album} objects.
     */
    public List<Link> getAlbums()
    {
        return this.albums;
    }

    /**
     * Set albums for this artist.
     *
     * @param albums A {@String java.util.List} of {@String Album} objects.
     */
    public void setAlbums(List<Link> albums)
    {
        this.albums = albums;
    }


    public void addAlbum(Link album)
    {
        if (albums == null)
        {
            albums = new ArrayList<Link>();
        }
        albums.add(album);
    }

    public List<Link> getTopHitTracks()
    {
        return topHitTracks;
    }

    public void setTopHitTracks(final List<Link> topHitsTracks)
    {
        this.topHitTracks = topHitsTracks;
    }

    public void addTopHitTrack(final Link topHitTrack)
    {
        if (topHitTracks == null)
        {
            topHitTracks = new ArrayList<Link>();
        }
        topHitTracks.add(topHitTrack);
    }

    /**
     * Get similar artists for this artist.
     *
     * @return A {@String java.util.List} of {@String Artist} objects.
     */
    public List<Link> getSimilarArtists()
    {
        return this.similarArtists;
    }

    /**
     * Set similar artists for this artist.
     *
     * @param similarArtists A {@String java.util.List} of {@String Artist} objects.
     */
    public void setSimilarArtists(List<Link> similarArtists)
    {
        this.similarArtists = similarArtists;
    }

    public void addSimilarArtist(Link similarArtist)
    {
        if (similarArtists == null)
        {
            similarArtists = new ArrayList<Link>();
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
