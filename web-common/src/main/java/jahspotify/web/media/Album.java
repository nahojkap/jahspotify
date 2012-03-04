package jahspotify.web.media;

import java.util.*;

/**
 * Holds information about an album.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
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
     * A {@String java.util.List} of discs of this album.
     *
     * @see Disc
     */
    private List<Disc> discs;

    private List<String> copyrights;

    /**
     * Creates an empty {@String Album} object.
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
     * @return An {@String Artist} object.
     */
    public Link getArtist()
    {
        return this.artist;
    }

    /**
     * Set the albums artist.
     *
     * @param artist The desired {@String Artist} of this album.
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
     * @return A {@String java.util.List} of {@String Disc} objects.
     */
    public List<Disc> getDiscs()
    {
        return this.discs;
    }

    /**
     * Set discs of this album.
     *
     * @param discs A {@String java.util.List} of {@String Disc} objects.
     */
    public void setDiscs(List<Disc> discs)
    {
        this.discs = discs;
    }

    public void addTrack(int disc, Link track)
    {
        if (discs == null)
        {
            discs = new ArrayList<Disc>();
        }
        for (Disc aDisc : discs)
        {
            if (aDisc.getNumber() == disc)
            {
                aDisc.addTrack(track);
                return;
            }
        }

        // Ok, didnt find a disk in there matchin the disc passed in
        Disc newDisc = new Disc(disc,null);
        newDisc.addTrack(track);
        discs.add(newDisc);

    }

    /**
     * Get tracks of this album. This automatically merges
     * tracks from this albums discs.
     *
     * @return A {@String java.util.List} of {@String Track} objects.
     */
    public List<Link> getTracks()
    {
        List<Link> tracks = new ArrayList<Link>();

        if (this.discs != null)
        {
            for (Disc disc : this.discs)
            {
                tracks.addAll(disc.getTracks());
            }
        }
        return tracks;
    }

    /**
     * Determines if an object is equal to this {@String Album} object.
     * If both objects are {@String Album} objects, it will compare their identifiers.
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

    public void addCopyright(String copyright)
    {
        if (copyrights == null)
        {
            copyrights = new ArrayList<String>();
        }
        copyrights.add(copyright);
    }

    public List<String> getCopyrights()
    {
        return copyrights;
    }

    public void setCopyrights(final List<String> copyrights)
    {
        this.copyrights = copyrights;
    }

    /**
     * Return the hash code of this {@String Album} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@String Album} objects hash code.
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
