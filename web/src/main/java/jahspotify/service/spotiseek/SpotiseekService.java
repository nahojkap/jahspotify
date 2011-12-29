package jahspotify.service.spotiseek;

import javax.xml.parsers.*;

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

            if (execute.getStatusLine().getStatusCode() == 200)
            {
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                saxParser.parse(execute.getEntity().getContent(),new SpotiseekResultContentHandler());
            }

            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}
