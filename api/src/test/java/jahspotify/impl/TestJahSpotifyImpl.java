package jahspotify.impl;

import java.util.concurrent.*;

import jahspotify.*;
import jahspotify.media.Link;
import junit.framework.TestCase;

/**
 * @author Johan Lindquist
 */
public class TestJahSpotifyImpl extends TestCase
{

    public void testIt() throws Exception
    {
        if (!Boolean.getBoolean("inidea"))
            {return;}

        // Do Test Here

        final CyclicBarrier cb = new CyclicBarrier(2);


        JahSpotify jahSpotify = JahSpotifyImpl.getInstance();

        jahSpotify.addConnectionListener(new AbstractConnectionListener()
        {
            @Override
            public void loggedIn()
            {
                try
                {
                    cb.await();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        jahSpotify.login("jlindquist", "spotify123ano1a12");

        cb.await();

        jahSpotify.initiateSearch(new Search(Query.token("alika")),new SearchListener()
        {
            @Override
            public void searchComplete(final SearchResult searchResult)
            {
            }
        });


        while(true)
        {
            Thread.sleep(10000);
        }

    }

}
