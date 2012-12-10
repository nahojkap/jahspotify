package jahspotify.storage;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.*;
import jahspotify.storage.media.MediaStorage;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class StorageAwareJahspotify extends JahSpotifyImpl
{
    private Log _log = LogFactory.getLog(StorageAwareJahspotify.class);

    private static JahSpotify _jahSpotify;

    private MediaStorage _mediaStorage;

    protected StorageAwareJahspotify()
    {
    }

    public void setMediaStorage(final MediaStorage mediaStorage)
    {
        _mediaStorage = mediaStorage;
    }

    public static JahSpotify getInstanceStorageBasedInstance()
    {
        if (_jahSpotify == null)
        {
            _jahSpotify = new StorageAwareJahspotify();
        }
        return _jahSpotify;
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        Album album;
        if (_mediaStorage != null)
        {
            album = _mediaStorage.readAlbum(uri);
            if (album != null)
            {
                _log.debug("Found album for " + uri + " in storage, will return that");
                return album;
            }
        }

        album = super.readAlbum(uri);

        if (_mediaStorage != null && album != null)
        {
            _mediaStorage.store(album);
        }
        return album;
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        Artist artist;
        if (_mediaStorage != null)
        {
            artist = _mediaStorage.readArtist(uri);
            if (artist != null)
            {
                _log.debug("Found artist for " + uri + " in storage, will return that");
                return artist;
            }
        }
        artist = super.readArtist(uri);

        if (_mediaStorage != null && artist != null)
        {
            _mediaStorage.store(artist);
        }
        return artist;
    }

    @Override
    public Image readImage(final Link uri)
    {
        Image image;
        if (_mediaStorage != null)
        {
            image = _mediaStorage.readImage(uri);
            if (image != null)
            {
                _log.debug("Found image for " + uri + " in storage, will return that");
                return image;
            }
        }

        image = super.readImage(uri);
        if (image != null && _mediaStorage != null)
        {
            _mediaStorage.store(image);
        }
        return image;
    }

    @Override
    public Playlist readPlaylist(final Link uri, final int trackIndex, final int numEntries)
    {
        Playlist playlist;
        if (trackIndex == 0 && numEntries == 0 && _mediaStorage != null)
        {
            playlist = _mediaStorage.readPlaylist(uri);
            if (playlist != null)
            {
                _log.debug("Found playlist for " + uri + " in storage, will return that");
                return playlist;
            }
        }

        playlist = super.readPlaylist(uri, trackIndex, numEntries);

        if (trackIndex == 0 && numEntries == 0 && _mediaStorage != null && playlist != null)
        {
            _mediaStorage.store(playlist);
        }

        return playlist;

    }

    @Override
    public Track readTrack(final Link uri)
    {
        Track track;
        if (_mediaStorage != null)
        {
            track = _mediaStorage.readTrack(uri);
            if (track != null)
            {
                _log.debug("Found track for " + uri + " in storage, will return that");
                return track;
            }
        }

        track = super.readTrack(uri);

        if (_mediaStorage != null && track != null)
        {
            _mediaStorage.store(track);
        }

        return track;

    }

    public boolean setStarredStateForTrack(final Link link, boolean starredState)
    {
        final boolean b = super.setStarredStateForTrack(link, starredState);

        _mediaStorage.deleteTrack(link);
        _mediaStorage.deletePlaylist(Link.create("jahspotify:starred"));

        return b;
    }

    @Override
    protected void imageLoadedCallback(final int token, final Link link, final ImageSize imageSize, final byte[] imageBytes)
    {
        // Store the dang thing
        if (_mediaStorage != null && imageBytes!= null)
        {
            _mediaStorage.store(new Image(link,imageBytes));
        }

        super.imageLoadedCallback(token,link,imageSize,imageBytes);
    }

    @Override
    protected void albumLoadedCallback(final int token, final Album album)
    {
        // Store the dang thing
        if (_mediaStorage != null && album != null)
        {
            _mediaStorage.store(album);
        }

        super.albumLoadedCallback(token, album);
    }

    @Override
    protected void artistLoadedCallback(final int token, final Artist artist)
    {
        // Store the dang thing
        if (_mediaStorage != null && artist != null)
        {
            _mediaStorage.store(artist);
        }

        super.artistLoadedCallback(token, artist);
    }
}
