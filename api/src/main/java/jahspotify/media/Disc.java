package jahspotify.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a disc of an album.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class Disc
{
    /**
     * The number of this disc.
     */
    private int number;

    /**
     * The name of this disc.
     */
    private String name;

    /**
     * A list of tracks on this disc.
     */
    private List<Track> tracks;

    /**
     * Create an empty {@link Disc} object.
     */
    public Disc()
    {
        this.number = -1;
        this.name = null;
        this.tracks = new ArrayList<Track>();
    }

    /**
     * Create a disc with the specified {@code number} and {@code name}.
     *
     * @param number The discs number.
     * @param name   The discs name.
     */
    public Disc(int number, String name)
    {
        this.number = number;
        this.name = name;
        this.tracks = new ArrayList<Track>();
    }

    /**
     * Get the discs number.
     *
     * @return An integer.
     */
    public int getNumber()
    {
        return this.number;
    }

    /**
     * Set the discs number.
     *
     * @param number The discs number.
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * Get the discs name.
     *
     * @return A string.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the discs name.
     *
     * @param name The discs name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get a list of tracks on this disc.
     *
     * @return A {@link List} of {@link Track} objects.
     */
    public List<Track> getTracks()
    {
        return this.tracks;
    }

    /**
     * Set the list of tracks on this disc.
     *
     * @param tracks A {@link List} of {@link Track} objects.
     */
    public void setTracks(List<Track> tracks)
    {
        this.tracks = tracks;
    }
}
