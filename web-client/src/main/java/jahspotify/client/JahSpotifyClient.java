package jahspotify.client;

import java.io.*;
import java.util.Arrays;

import com.google.gson.Gson;
import jahspotify.web.queue.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyClient
{
    private static String baseURL = "http://localhost:8080/jahspotify/";

    public static void main(String[] args) throws Exception
    {
        // addTrack();

        // command line:

        // jahspotify -u <url> --url=<url> queue <-l --list> <-a <track-id> --add=<track-id>> <-d <track-id> --del=<track-id>
        // jahspotify -u <url> --url=<url> ctrl <-s --skip> <-p --pause> <-r --resume>
        // jahspotify -u <url> --url=<url> track <track-id>
        // jahspotify -u <url> --url=<url> album <album-id>
        // jahspotify -u <url> --url=<url> artist <artist-id>
        // jahspotify -u <url> --url=<url> playlist <playlist-id>
        // jahspotify -u <url> --url=<url> image -o <output-file> <image-id>

        QueueConfiguration queueConfiguration = new QueueConfiguration();
        queueConfiguration.setRepeatCurrentTrack(false);
        queueConfiguration.setRepeatCurrentQueue(false);


        final String s = postData(baseURL + "queue/configuration", queueConfiguration);

        QueueConfiguration newQueueConfiguration = deserialize(s,QueueConfiguration.class);

    }

    private static <T> T  deserialize(final String s, final Class<T> classOfT)
    {
        Gson gson = new Gson();
        return gson.fromJson(s,classOfT);
    }

    private static void addTrack() throws IOException
    {
        QueueTracksRequest queueTracksRequest = new QueueTracksRequest();
        queueTracksRequest.setAutoPlay(true);
        queueTracksRequest.setURIQueue(Arrays.asList("spotify:track:6UaRii9AH6Zss9xNMEQ2M9", "spotify:track:34q1KaLX8h73xE06xPBmNB", "spotify:track:52JyHLUiugFECIYBWM2qdh"));

        final String s = postData("http://localhost:8080/jahspotify/queue/", queueTracksRequest);

    }

    private static String postData(final String url, final Object obj) throws IOException
    {
        Gson gson = new Gson();
        String data = gson.toJson(obj);

        HttpClient httpClient = new DefaultHttpClient();

        final HttpPost httpPost = new HttpPost(url);
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContentType("application/data");
        basicHttpEntity.setContent(new ByteArrayInputStream(data.getBytes()));
        httpPost.setEntity(basicHttpEntity);
        final HttpResponse execute = httpClient.execute(httpPost);

        if (execute.getStatusLine().getStatusCode() == 200)
        {
            final InputStream content = execute.getEntity().getContent();
            BufferedReader stringReader = new BufferedReader(new InputStreamReader(content));
            StringBuffer sb = new StringBuffer();
            String tmp = stringReader.readLine();
            while (tmp != null)
            {
                sb.append(tmp);
                tmp = stringReader.readLine();
            }


            return sb.toString();

        }

        return  null;
    }
}
