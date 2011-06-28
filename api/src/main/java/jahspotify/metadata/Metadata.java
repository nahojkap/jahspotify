package jahspotify.metadata;

import java.io.*;

import com.google.gson.*;
import jahspotify.media.Link;
import jahspotify.metadata.lookup.*;
import jahspotify.metadata.search.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Johan Lindquist
 */
public class Metadata
{
    final String searchBaseURL = "http://ws.spotify.com/search/1/";
    final String lookupBaseURL = "http://ws.spotify.com/lookup/1/.json?uri=";

    public static void main(String[] args)
    {
        Metadata md = new Metadata();
//        final AlbumSearchResult albums = md.searchAlbums("foo");
//        final TrackSearchResult tracks = md.searchTracks("foo");
//        final ArtistSearchResult artistSearchResult = md.searchArtist("foo");
//        final ArtistMediaLookup artistLookupResult = md.lookupArtist(Link.create("spotify:artist:4YrKBkKSVeqDamzBPWVnSJ"));
        final AlbumMediaLookup albumMediaLookup = md.lookupAlbum(Link.create("spotify:album:6G9fHYDCoyEErUkHrFYfs4"));
        System.out.println("albumMediaLookup = " + albumMediaLookup);
    }

    public AlbumSearchResult searchAlbums(String query)
    {
        try
        {
            // http://ws.spotify.com/search/1/album?q=foo&page=2

            return getData(searchBaseURL + "album.json?q=" + encodeQuery(query), AlbumSearchResult.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }

    public ArtistSearchResult searchArtist(String query)
          {
              try
              {
                  // http://ws.spotify.com/search/1/album?q=foo&page=2
                  return getData(searchBaseURL + "artist.json?q=" + encodeQuery(query), ArtistSearchResult.class);
              }
              catch (IOException e)
              {
                  e.printStackTrace();
                  throw new RuntimeException(e.getMessage());
              }


          }


    public TrackSearchResult searchTracks(String query)
       {
           try
           {
               // http://ws.spotify.com/search/1/album?q=foo&page=2
               final TrackSearchResult trackSearchResult = getData(searchBaseURL + "track.json?q=" + encodeQuery(query), TrackSearchResult.class);
               return trackSearchResult;
           }
           catch (IOException e)
           {
               e.printStackTrace();
               throw new RuntimeException(e.getMessage());
           }


       }

    public ArtistMediaLookup lookupArtist(Link uri)
    {
        try
        {
            final LookupResult data = getData(lookupBaseURL + uri.asString() + "&extras=albumdetail", LookupResult.class);
            return data.getArtist();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());

        }
    }

    public AlbumMediaLookup lookupAlbum(Link uri)
    {
        try
        {
            final LookupResult data = getData(lookupBaseURL + uri.asString() + "&extras=trackdetail", LookupResult.class);
            return data.getAlbum();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());

        }
    }

    private String encodeQuery(final String query)
    {
        return query.replaceAll(" ","\\+");
    }

    private static <T> T getData(final String url, final Class<T> classOfT) throws IOException
    {
        System.out.println("url = " + url);
        HttpClient httpClient = new DefaultHttpClient();

        final HttpGet httpGet = new HttpGet(url);

        final HttpResponse execute = httpClient.execute(httpGet);

        if (execute.getStatusLine().getStatusCode() == 200)
        {
            final InputStream content = execute.getEntity().getContent();
            BufferedReader stringReader = new BufferedReader(new InputStreamReader(content));
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

            return gson.fromJson(stringReader, classOfT);
        }

        return  null;
    }


}
