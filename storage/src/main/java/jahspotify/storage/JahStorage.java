package jahspotify.storage;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface JahStorage
{
    public void store(Track track);
    public void store(Artist artist);
    public void store(Album album);
    public void store(Playlist playlist);
    public void store(Image image);
}
