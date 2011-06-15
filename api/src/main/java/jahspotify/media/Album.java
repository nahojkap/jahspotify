package jahspotify.media;

import java.util.*;

import jahspotify.util.Hex;

/**
 * Holds information about an album.
 *
 * @author Felix Bruns <felixbruns@web.de>
 */
public class Album extends Media
{
    /**
     * Name of this album.
     */
    private String name;

    /**
     * Artist of this album.
     */
    private Link artist;

    /**
     * The identifier for this albums cover image (32-character string).
     */
    private Link cover;

    /**
     * The type of this album (compilation, album, single).
     */
    private AlbumType type;

    /**
     * The review of this album.
     */
    private String review;

    /**
     * Release year of this album.
     */
    private int year;

    /**
     * A {@link List} of discs of this album.
     *
     * @see Disc
     */
    private List<Disc> discs;

    /**
     * Similar albums of this album.
     */
    private List<Link> similarAlbums;

    /**
     * Creates an empty {@link Album} object.
     */
    public Album()
    {
        this.name = null;
        this.artist = null;
        this.cover = null;
        this.type = null;
        this.review = null;
        this.year = -1;
        this.discs = new ArrayList<Disc>();
        this.similarAlbums = new ArrayList<Link>();
    }

    /**
     * Get the albums name.
     *
     * @return The name of this album.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the albums name.
     *
     * @param name The desired name of this album.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the albums artist.
     *
     * @return An {@link Artist} object.
     */
    public Link getArtist()
    {
        return this.artist;
    }

    /**
     * Set the albums artist.
     *
     * @param artist The desired {@link Artist} of this album.
     */
    public void setArtist(Link artist)
    {
        this.artist = artist;
    }

    /**
     * Get the albums cover image identifier.
     *
     * @return A 32-character image identifier.
     */
    public Link getCover()
    {
        return this.cover;
    }

    /**
     * Set the albums cover image identifier.
     *
     * @param cover A 40-character image identifier.
     */
    public void setCover(Link cover)
    {
        this.cover = cover;
    }

    /**
     * Get the albums type.
     *
     * @return One of "compilation", "album", "single" or unknown.
     */
    public AlbumType getType()
    {
        return this.type;
    }

    /**
     * Set the albums type.
     *
     * @param type One of "compilation", "album", "single" or unknown.
     */
    public void setType(AlbumType type)
    {
        this.type = type;
    }

    /**
     * Get the albums review.
     *
     * @return A review of this album.
     */
    public String getReview()
    {
        return this.review;
    }

    /**
     * Set the albums review.
     *
     * @param review A review of this album.
     */
    public void setReview(String review)
    {
        this.review = review;
    }

    /**
     * Get the albums release year.
     *
     * @return An integer denoting the release year or -1 if not available.
     */
    public int getYear()
    {
        return this.year;
    }

    /**
     * Set the albums release year.
     *
     * @param year A positive integer denoting the release year.
     */
    public void setYear(int year)
    {
        /* Check if year is valid. Years B.C. are not supported :-P */
        if (year < 0)
        {
            throw new IllegalArgumentException("Expecting a positive year.");
        }

        this.year = year;
    }

    /**
     * Get discs of this album.
     *
     * @return A {@link List} of {@link Disc} objects.
     */
    public List<Disc> getDiscs()
    {
        return this.discs;
    }

    /**
     * Set discs of this album.
     *
     * @param discs A {@link List} of {@link Disc} objects.
     */
    public void setDiscs(List<Disc> discs)
    {
        this.discs = discs;
    }

    /**
     * Set discs for this album.
     *
     * @param discs A {@link List} of {@link Disc} objects.
     */
    public void setTracks(List<Disc> discs)
    {
        this.discs = discs;
    }

    /**
     * Get tracks of this album. This automatically merges
     * tracks from this albums discs.
     *
     * @return A {@link List} of {@link Track} objects.
     */
    public List<Track> getTracks()
    {
        List<Track> tracks = new ArrayList<Track>();

        for (Disc disc : this.discs)
        {
            tracks.addAll(disc.getTracks());
        }

        return tracks;
    }

    /**
     * Get similar albums for this album.
     *
     * @return A {@link List} of {@link Album} objects.
     */
    public List<Link> getSimilarAlbums()
    {
        return this.similarAlbums;
    }

    /**
     * Set similar albums for this album.
     *
     * @param similarAlbums A {@link List} of {@link Album} objects.
     */
    public void setSimilarAlbums(List<Link> similarAlbums)
    {
        this.similarAlbums = similarAlbums;
    }

    /**
     * Determines if an object is equal to this {@link Album} object.
     * If both objects are {@link Album} objects, it will compare their identifiers.
     *
     * @param o Another object to compare.
     * @return true of the objects are equal, false otherwise.
     */
    public boolean equals(Object o)
    {
        if (o instanceof Album)
        {
            Album a = (Album) o;

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
     * Return the hash code of this {@link Album} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@link Album} objects hash code.
     */
    public int hashCode()
    {
        return (this.id != null) ? this.id.hashCode() : 0;
    }

    public String toString()
    {
        return String.format("[Album: %s, %s]", this.artist, this.name);
    }
}
