package jahspotify.service;

import junit.framework.TestCase;

/**
 * @author Johan Lindquist
 */
public class TestSpotiseekService extends TestCase
{
    public void testSimpleSearch() throws Exception
    {
        SpotiseekService spotiseekService = new SpotiseekService();
        final SpotiseekResult spotiseekResult = spotiseekService.search("alika");
        System.out.println("spotiseekResult = " + spotiseekResult);

    }
}
