package jahspotify.client;

import java.io.*;
import java.util.Arrays;

import com.google.gson.Gson;
import jahspotify.web.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Johan Lindquist
 */
public class AddTracksHelper
{
    public static void main(String[] args) throws Exception
    {
        Gson gson = new Gson();

        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setAutoPlay(true);
        queueRequest.setURIQueue(Arrays.asList("spotify:track:7mliwEVqxIuwLmHdTXlBrx", "spotify:track:2sEajlZVLItzPKLO5yTVnP","spotify:track:46tnXk2k4uoy0skIcC69Fy"));

        String json = gson.toJson(queueRequest);

        HttpClient httpClient = new DefaultHttpClient();

        final HttpPost httpPost = new HttpPost("http://localhost:8080/jahspotify/queue/");
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContentType("application/json");
        basicHttpEntity.setContent(new ByteArrayInputStream(json.getBytes()));
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

            BasicResponse basicResponse = gson.fromJson(sb.toString(), BasicResponse.class);
            System.out.println("basicResponse.getResponseStatus() = " + basicResponse.getResponseStatus());

        }



    }
}
