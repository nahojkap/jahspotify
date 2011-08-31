package jahspotify.service;

import java.util.concurrent.*;
import javax.annotation.PostConstruct;

import jahspotify.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class SearchEngine
{
    @Autowired
    private JahSpotifyService _jahSpotifyService;

    private JahSpotify _jahSpotify;

    @PostConstruct
    public void initialize()
    {
        _jahSpotify = _jahSpotifyService.getJahSpotify();
    }

    public SearchResult search(Search search)
    {
        try
        {
            final BlockingQueue<SearchResult> resultQueue = new ArrayBlockingQueue<SearchResult>(1);
            _jahSpotify.initiateSearch(search, new SearchListener()
            {
                @Override
                public void searchComplete(final SearchResult searchResult)
                {
                    try
                    {
                        resultQueue.put(searchResult);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            return resultQueue.poll(10, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

}
