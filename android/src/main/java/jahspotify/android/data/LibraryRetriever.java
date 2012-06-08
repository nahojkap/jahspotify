package jahspotify.android.data;

import java.io.*;
import java.util.*;

import android.util.Log;
import jahspotify.client.JahSpotifyClient;
import jahspotify.web.media.*;

/**
 * @author Johan Lindquist
 */
public class LibraryRetriever
{
    public static String baseURL;

    private static JahSpotifyClient jahSpotifyClient;
    private static final String TAG = "LibraryRetriever";

    private static Map<Link,FullTrack> _fullTrackCache = new HashMap<Link, FullTrack>();
    private static Map<Link,Playlist> _playlistCache = new HashMap<Link, Playlist>();
    private static Map<String, LibraryEntry> _folderCache = new HashMap<String, LibraryEntry>();
    private static final Link ROOT_FOLDER = new Link("jahspotify:folder:ROOT", Link.Type.FOLDER);

    public static void initialize(String host, int port)
    {
        baseURL = "http://" + host + ":" + port + "/jahspotify/";
        jahSpotifyClient = new JahSpotifyClient(baseURL);
    }

    public static LibraryEntry getRoot(int levels) throws IOException
    {
        return getEntry(ROOT_FOLDER, levels);
    }

    private static String makeCacheName(final Link link, final int levels)
    {
        return "" + link + "" + levels;
    }

    public static InputStream getImage(final Link link) throws Exception
    {
        InputStream inputStream = ImageCache.readImage(link);
        if (inputStream == null)
        {
            try
            {
                Log.d("LibraryRetriever", "Retrieving image: " + link);
                final Image image = jahSpotifyClient.readImage(link.getId(), false);
                ImageCache.storeImage(link,image.getBytes());
                inputStream = new ByteArrayInputStream(image.getBytes());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.d(TAG, "Error retrieving image for link: " + link, e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return inputStream;
    }

    public static LibraryEntry getEntry(final Link link, int levels) throws IOException
    {
        final String cacheName = makeCacheName(link,levels);
        LibraryEntry entry = _folderCache.get(cacheName);
        if (entry == null)
        {
            entry = jahSpotifyClient.readFolder(link.getId(),levels);
            if (entry != null)
            {
                _folderCache.put(cacheName, entry);
            }
        }
        return entry;
    }

    public static Track getTrack(final Link trackLink) throws IOException
    {
        return jahSpotifyClient.readTrack(trackLink.getId());
    }

    public static Playlist getPlaylist(final Link link) throws IOException
    {
        Playlist playlist = _playlistCache.get(link);
        if (playlist == null)
        {
            playlist = jahSpotifyClient.readPlaylist(link.getId());
            if (playlist != null)
            {
                _playlistCache.put(link,playlist);
            }
        }
        return playlist;
    }

    public static void queue(final Link uri) throws IOException
    {
        jahSpotifyClient.queueTracks(true, Arrays.asList(uri.getId()));
    }

    public static Album getAlbum(final Link albumLink) throws IOException
    {
        return jahSpotifyClient.readAlbum(albumLink.getId());
    }

    public static FullTrack getFullTrack(final Link trackLink) throws IOException
    {
        FullTrack fullTrack = _fullTrackCache.get(trackLink);
        if (fullTrack == null)
        {
            fullTrack = jahSpotifyClient.readFullTrack(trackLink.getId());
            if (fullTrack.isComplete())
            {
                _fullTrackCache.put(trackLink,fullTrack);
            }
        }
        return fullTrack;
    }
}
