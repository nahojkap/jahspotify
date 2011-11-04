package jahspotify.android.data;

import java.io.*;
import java.util.Arrays;

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
    // private static String baseURL = "http://10.40.42.41:8080/jahspotify/";
    private static String baseURL = "http://192.168.0.11:8080/jahspotify/";
    private static JahSpotifyClient jahSpotifyClient = new JahSpotifyClient(baseURL);

    public static Library.Entry getRoot(int levels) throws IOException
    {
        return getEntry(new Link("jahspotify:folder:ROOT","folder"), levels);
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
}
