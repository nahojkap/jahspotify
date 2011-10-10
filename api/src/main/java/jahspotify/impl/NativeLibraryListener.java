package jahspotify.impl;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface NativeLibraryListener
{
    public void synchStarted(int numPlaylists);
    public void synchCompleted();
    public void startFolder(String folderName, final long folderID);
    public void endFolder();
    public void playlist(final String name, final String link);
    public void metadataUpdated(String link);
}
