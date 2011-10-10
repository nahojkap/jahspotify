package jahspotify;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface PlaylistListener
{
    public void synchStarted(int numPlaylists);
    public void synchCompleted();
    public void startFolder(final Link link, String folderName);
    public void endFolder(final Link link);
    public void playlist(final Link link, final String name);
    public void metadataUpdated(final Link link);

}
