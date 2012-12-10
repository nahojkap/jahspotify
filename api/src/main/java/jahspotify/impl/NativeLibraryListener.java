package jahspotify.impl;

/**
 * @author Johan Lindquist
 */
public interface NativeLibraryListener
{
    public void synchStarted(int numPlaylists);
    public void synchCompleted();
    public void startFolder(String folderName, final long folderID, final int index);
    public void endFolder(int index);
    public void playlist(final String name, final String link, final int index);
    public void metadataUpdated(String link);
}
