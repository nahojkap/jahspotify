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
    public void startFolder(final Link link, final String folderName, int index)
    {
    }

    @Override
    public void endFolder(final Link link, int index)
    {
    }

    @Override
    public void playlist(final Link link, final String name, int index)
    {
    }

    @Override
    public void metadataUpdated(final Link link)
    {
    }

}
