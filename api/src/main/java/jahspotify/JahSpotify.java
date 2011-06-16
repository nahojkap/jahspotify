package jahspotify;

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

    public Artist readArtist(Link link);

    public Track readTrack(Link link);

    public Album readAlbum(Link link);

    public Image readImage(Link link);

    public Playlist readPlaylist(Link link);

    public List<Track> readTracks(List<Link> links);

    public List<Track> readTracks(Link... uris);

    public Library retrieveLibrary();

    public int pause();

    public int resume();

    public int play(Link uri);

    public User getUser();

    public void addPlaybackListener(PlaybackListener playbackListener);

    public void addPlaylistListener(PlaylistListener playlistListener);

    public void addConnectionListener(ConnectionListener connectionListener);

    public boolean isStarted();

    public void stop();
}
