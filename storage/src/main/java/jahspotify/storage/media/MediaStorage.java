package jahspotify.storage.media;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface MediaStorage
{
    public void store(Track track);
    public Track readTrack(Link uri);
    public void deleteTrack(Link uri);
    public void store(Artist artist);
    public Artist readArtist(Link uri);
    public void store(Album album);
    public Album readAlbum(Link uri);
    public void store(Playlist playlist);
    public Playlist readPlaylist(Link uri);
    public void store(Image image);
    public Image readImage(Link uri);
}
