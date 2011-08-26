package jahspotify.impl;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface NativePlaylistListener
{
    public void synchStarted(int numPlaylists);
    public void synchCompleted();
    public void startFolder(String folderName, final long folderID);
    public void endFolder();
    public void playlist(final String name, final String link);
    public void metadataUpdated();

    public void track(final Track track);
    public void playlist(final Playlist playlist);
    public void album(final Album album);
    public void image(final Image image);
    public void artist(final Artist artist);
}
