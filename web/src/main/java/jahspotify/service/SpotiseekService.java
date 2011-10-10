package jahspotify.service;

import java.io.*;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Johan Lindquist
 */
public class SpotiseekService
{


    // private String _baseURL = "http://www.spotiseek.com/mixtape.php?popular=true&artist=alika";
    private String _baseURL = "http://www.spotiseek.com/service/searchSpotify.php?popular=true&artist=alika";

    public SpotiseekResult search(String query)
    {
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            final HttpPost httpPost = new HttpPost(_baseURL);
            final HttpResponse execute = httpClient.execute(httpPost);

            System.out.println("execute = " + execute.getStatusLine().getStatusCode());

            final InputStream content = execute.getEntity().getContent();
            byte[] buffer = new byte[16000];




            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(),e);
        }
    }


}
