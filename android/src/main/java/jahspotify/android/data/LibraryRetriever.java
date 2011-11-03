package jahspotify.android.data;

import java.io.*;

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
    public static Library.Entry getRoot(int levels) throws IOException
    {
        return getEntry("jahspotify:folder:ROOT", levels);
    }

    public static Library.Entry getEntry(final String uri, int levels) throws IOException
    {
        final JahSpotifyClient jahSpotifyClient = new JahSpotifyClient("http://10.40.42.41:8080/jahspotify/");
        return jahSpotifyClient.readFolder(uri,levels);
    }

    public static Track getTrack(final String trackLink) throws IOException
    {
        final JahSpotifyClient jahSpotifyClient = new JahSpotifyClient("http://10.40.42.41:8080/jahspotify/");
        return jahSpotifyClient.readTrack(trackLink);
    }

    public static Playlist getPlaylist(final String uri) throws IOException
    {
        final JahSpotifyClient jahSpotifyClient = new JahSpotifyClient("http://10.40.42.41:8080/jahspotify/");
        return jahSpotifyClient.readPlaylist(uri);
    }
}
