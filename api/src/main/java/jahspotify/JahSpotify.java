package jahspotify;

import java.io.OutputStream;
import java.util.List;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface JahSpotify
{
    /**
     *
     * @param username
     * @param password
     */
    public void start(String username, String password);

    public Artist readArtist(String uri);

    public Album readAlbum(String uri);

    public Track readTrack(String uri);

    public byte[] readImage(String uri);

    public Playlist readPlaylist(String uri);

    public List<Track> readTracks(String... uris);

    public int pause();

    public int resume();

    public int play(String uri);

    public User getUser();

    public void addPlaybackListener(PlaybackListener playbackListener);

    public void addPlaylistListener(PlaylistListener playlistListener);

    public void addConnectionListener(ConnectionListener connectionListener);

    public boolean isStarted();

    public void stop();
}
