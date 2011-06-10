package jahspotify.media;

import jahspotify.util.Hex;

/**
 * Holds information about a file.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class File implements Comparable<File>
{
    public static final int BITRATE_96 = 96000;
    public static final int BITRATE_160 = 160000;
    public static final int BITRATE_320 = 320000;

    /**
     * The files 40-character hex identifier.
     */
    private String id;

    /**
     * The files format. e.g. Ogg Vorbis,320000,...
     */
    private String format;

    /**
     * Creates an empty {@link File} object.
     */
    protected File()
    {
        this.id = null;
        this.format = null;
    }

    /**
     * Creates a {@link File} object with the specified {@code id} and {@code format}.
     *
     * @param id     Id of the file.
     * @param format Format of the file.
     * @throws IllegalArgumentException If the given id is invalid.
     */
    public File(String id, String format)
    {
        /* Check if id string is valid. */
        if (id == null || id.length() != 40 || !Hex.isHex(id))
        {
            throw new IllegalArgumentException("Expecting a 40-character hex string.");
        }

        /* Set object properties. */
        this.id = id;
        this.format = format;
    }

    /**
     * Get the files identifier.
     *
     * @return A 40-character identifier.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Set the files identifier.
     *
     * @param id A 40-character identifier.
     * @throws IllegalArgumentException If the given id is invalid.
     */
    public void setId(String id)
    {
        /* Check if id string is valid. */
        if (id == null || id.length() != 40 || !Hex.isHex(id))
        {
            throw new IllegalArgumentException("Expecting a 40-character hex string.");
        }

        this.id = id;
    }

    /**
     * Get the files format.
     *
     * @return A format string including codec and bitrate.
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * Set the files format.
     *
     * @param format A format string including codec and bitrate.
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * Get the files bitrate
     *
     * @return An integer.
     */
    public int getBitrate()
    {
        return Integer.parseInt(this.format.split(",")[1]);
    }

    /**
     * Determines if an object is equal to this {@link File} object.
     * If both objects are {@link File} objects, it will compare their identifiers.
     *
     * @param o Another object to compare.
     * @return true of the objects are equal, false otherwise.
     */
    public boolean equals(Object o)
    {
        if (o instanceof File)
        {
            File f = (File) o;

            if (this.id.equals(f.id))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the hash code of this {@link File} object. This will give the value returned
     * by the {@code hashCode} method of the identifier string.
     *
     * @return The {@link File} objects hash code.
     */
    public int hashCode()
    {
        return (this.id != null) ? this.id.hashCode() : 0;
    }

    /**
     * Compares two {@link File} objects. A file is considered greater than another file,
     * if its bitrate is higher.
     *
     * @param f Another {@link File} to compare.
     * @return A negative integer, zero, or a positive integer as this file is less than,
     *         equal to, or greater than the specified file.
     */
    public int compareTo(File f)
    {
        return this.getBitrate() - f.getBitrate();
    }

    public String toString()
    {
        return String.format("[File: %s]", this.format);
    }
}
