package jahspotify.storage.media;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import jahspotify.media.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "in-memory")
public class InMemoryMediaStorage extends FileMediaStorage implements MediaStorage
{
    private Map<Link,Image> _imageStore = new ConcurrentHashMap<Link, Image>();
    private Map<Link,Track> _trackStore = new ConcurrentHashMap<Link, Track>();
    private Map<Link,Album> _albumStore = new ConcurrentHashMap<Link, Album>();
    private Map<Link,Artist> _artistStore = new ConcurrentHashMap<Link, Artist>();
    private Map<Link,Playlist> _playlistStore = new ConcurrentHashMap<Link, Playlist>();

    @PostConstruct
    public void initialize()
    {
        super.initialize();
    }

    @Override
    public void store(final Track track)
    {
        _trackStore.put(track.getId(),track);
        super.store(track);
    }

    @Override
    public Track readTrack(final Link uri)
    {
        final Track track = _trackStore.get(uri);
        if (track == null)
        {
            return super.readTrack(uri);
        }
        return track;
    }

    @Override
    public void deleteTrack(final Link uri)
    {
        _trackStore.remove(uri);
        super.deleteTrack(uri);
    }

    @Override
    public void store(final Artist artist)
    {
        _artistStore.put(artist.getId(),artist);
        super.store(artist);
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        final Artist artist = _artistStore.get(uri);
        if (artist == null)
        {
            return super.readArtist(uri);
        }
        return artist;
    }

    @Override
    public void store(final Album album)
    {
        _albumStore.put(album.getId(),album);
        super.store(album);
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        final Album album = _albumStore.get(uri);
        if (album == null)
        {
            return super.readAlbum(uri);
        }
        return album;
    }

    @Override
    public void store(final Playlist playlist)
    {
        _playlistStore.put(playlist.getId(),playlist);
        super.store(playlist);
    }

    @Override
    public Playlist readPlaylist(final Link uri)
    {
        final Playlist playlist = _playlistStore.get(uri);
        if (playlist == null)
        {
            return super.readPlaylist(uri);
        }
        return playlist;
    }

    @Override
    public void store(final Image image)
    {
        _imageStore.put(image.getId(),image);
        super.store(image);

    }

    @Override
    public Image readImage(final Link uri)
    {
        final Image image = _imageStore.get(uri);
        if (image == null)
        {
            return super.readImage(uri);
        }
        return image;
    }
}
