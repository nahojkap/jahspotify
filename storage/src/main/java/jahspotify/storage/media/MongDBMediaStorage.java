package jahspotify.storage.media;

import jahspotify.media.*;
import jahspotify.storage.media.MediaStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "mongo")
public class MongDBMediaStorage implements MediaStorage
{
    @Override
    public void store(final Track track)
    {
    }

    @Override
    public Track readTrack(final Link uri)
    {
        return null;
    }

    @Override
    public void deleteTrack(final Link uri)
    {
    }

    @Override
    public void store(final Artist artist)
    {
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        return null;
    }

    @Override
    public void store(final Album album)
    {
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        return null;
    }

    @Override
    public void store(final Playlist playlist)
    {
    }

    @Override
    public Playlist readPlaylist(final Link uri)
    {
        return null;
    }

    @Override
    public void store(final Image image)
    {
    }

    @Override
    public Image readImage(final Link uri)
    {
        return null;
    }
}
