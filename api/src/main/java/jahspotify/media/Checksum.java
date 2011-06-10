package jahspotify.media;

import java.util.zip.Adler32;

import jahspotify.util.Hex;

/**
 * Subclass of {@link Adler32}, supplying methods to calculate
 * a checksum of different media objects.
 *
 * @author Felix Bruns <felixbruns@web.de>
 */
public class Checksum extends Adler32
{
    /**
     * Update the checksum with a {@link Playlist}.
     *
     * @param playlist A {@link Playlist} object.
     */
    public void update(Playlist playlist)
    {
        this.update(Hex.toBytes(playlist.getId()));
        this.update((byte) 0x02);
    }

    /**
     * Update the checksum with a {@link Track}.
     *
     * @param track A {@link Track} object.
     */
    public void update(Track track)
    {
        this.update(Hex.toBytes(track.getId()));
        this.update((byte) 0x01);
    }
}
