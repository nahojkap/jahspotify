package jahspotify.android.data;

import java.io.*;

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

    public String[] getRoot()
    {
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://localhost:8080/jahspotify/folder/jahspotify:folder:ROOT?level=1");

            final HttpResponse execute = httpClient.execute(get);

            if (execute.getStatusLine().getStatusCode() == 200)
            {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()));

                String tmp = bufferedReader.readLine();

                final StringBuilder rootList = new StringBuilder();
                rootList.append(tmp);

                JSONObject json = new JSONObject(rootList.toString());
                final JSONArray children = json.getJSONArray("children");

                for (int i = 0; i < children.length(); i++)
                {

                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return new String[0];
    }

}
