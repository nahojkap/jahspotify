package jahspotify;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface PlaylistListener
{
    public void synchStarted(int numPlaylists);
    public void synchCompleted();
    public void startFolder(final Link link, String folderName, int index);
    public void endFolder(final Link link, int index);
    public void playlist(final Link link, final String name, int index);
    public void metadataUpdated(final Link link);

}
