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
    void start(String username, String password);

    int readImage(String uri, OutputStream outputStream);

    Album readAlbum(String uri);

    Track readTrack(String uri);

    byte[] readImage(String uri);

    Playlist readPlaylist(String uri);

    List<Track> readTracks(String... uris);

    int pause();

    int resume();

    int play(String uri);

    User getUser();

    void addPlaybackListener(PlaybackListener playbackListener);

    void addPlaylistListener(PlaylistListener playlistListener);

    void addConnectionListener(ConnectionListener connectionListener);

    boolean isStarted();

    void stop();
}
