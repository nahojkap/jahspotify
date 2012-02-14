package jahspotify.android.data;

import java.io.*;
import java.util.*;

import android.util.Log;
import jahspotify.client.JahSpotifyClient;
import jahspotify.web.media.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

/**
 * @author Johan Lindquist
 */
public class LibraryRetriever
{
    public static String baseURL = "http://192.168.0.11:8080/jahspotify/";

    private static JahSpotifyClient jahSpotifyClient;

    public static void initialize(String host, int port)
    {
        baseURL = "http://" + host + ":" + port + "/jahspotify/";
        jahSpotifyClient = new JahSpotifyClient(baseURL);
    }

    public static Library.Entry getRoot(int levels) throws IOException
    {
        return getEntry(new Link("jahspotify:folder:ROOT", Link.Type.FOLDER), levels);
    }

    public static InputStream getImage(final Link link) throws IOException
    {
        Log.d("LibraryRetriever","Retrieving image: " + link);
        final Image image = jahSpotifyClient.readImage(link.getId(), true);
        return image.getInputStream();
    }

    public static Library.Entry getEntry(final Link link, int levels) throws IOException
    {
        return jahSpotifyClient.readFolder(link.getId(),levels);
    }

    public static Track getTrack(final Link trackLink) throws IOException
    {
        return jahSpotifyClient.readTrack(trackLink.getId());
    }

    public static Playlist getPlaylist(final Link link) throws IOException
    {
        return jahSpotifyClient.readPlaylist(link.getId());
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
        return jahSpotifyClient.readFullTrack(trackLink.getId());
    }
}
