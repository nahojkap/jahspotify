package jahspotify.service;

import java.io.*;
import java.net.*;
import java.util.List;

import jahspotify.mp3.MPG123Player;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class RSSFeedManager
{
    public void addFeed(String path, URL url)
    {
    }

    public List<String> retrievePaths(String path)
    {
        return null;
    }

    public URL getFeed(String path)
    {
        return null;
    }


    public static void main(String[] args) throws Exception
    {
/*
        //URL feedSource = new URL("http://feeds.feedburner.com/Storynory?format=xml");
        URL feedSource = new URL("http://feeds.feedburner.com/javaposse?format=xml");
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries)
        {
            List<SyndEnclosure> enclosures = entry.getEnclosures();
            for (SyndEnclosure enclosure : enclosures)
            {
                final String url = enclosure.getUrl();

                System.out.println("url = " + url);
                final String s = playMP3(url);

                System.out.println("s = " + s);

                if (s != null)
                {
                    final Process exec = Runtime.getRuntime().exec(new String[]{"mpg123", s});
                }
            }

        }
        MPG123Player mpg123Player = new MPG123Player();
        mpg123Player.playMP3(new URL("http://localhost/Portishead-Strangers.mp3"),File.createTempFile("jahspotify",".mp3"));
        Thread.sleep(5000);
        mpg123Player.stop();
*/

    }


}
