package jahspotify.impl;

import jahspotify.PlaylistListener;
import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public abstract class AbstractPlaylistListener implements PlaylistListener
{
    @Override
    public void synchStarted(final int numPlaylists)
    {
    }

    @Override
    public void synchCompleted()
    {
    }

    @Override
    public void startFolder(final String folderName, final long folderID)
    {
    }

    @Override
    public void endFolder()
    {
    }

    @Override
    public void playlist(final String name, final String link)
    {
    }

    @Override
    public void metadataUpdated()
    {
    }

    @Override
    public void track(final Track track)
    {
    }

    @Override
    public void playlist(final Playlist playlist)
    {
    }

    @Override
    public void album(final Album album)
    {
    }

    @Override
    public void image(final Image image)
    {
    }

    @Override
    public void artist(final Artist artist)
    {
    }
}
