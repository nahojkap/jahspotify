package jahspotify.web.media;

import java.util.*;

/**
 * Holds information about a disc of an album.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
 */
public class Disc
{
    /**
     * The number of this disc.
     */
    private int number;

    /**
     * A list of tracks on this disc.
     */
    private List<String> tracks;

    /**
     * Create an empty {@String Disc} object.
     */
    public Disc()
    {
        this.number = -1;
        this.tracks = new ArrayList<String>();
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
        this.tracks = new ArrayList<String>();
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
     * Get a list of tracks on this disc.
     *
     * @return A {@String java.util.List} of {@String Track} objects.
     */
    public List<String> getTracks()
    {
        return this.tracks;
    }

    /**
     * Set the list of tracks on this disc.
     *
     * @param tracks A {@String java.util.List} of {@String Track} objects.
     */
    public void setTracks(List<String> tracks)
    {
        this.tracks = tracks;
    }

    public void addTrack(final String track)
    {
        if (tracks == null)
        {
            tracks = new ArrayList<String>();
        }
        tracks.add(track);
    }
}
