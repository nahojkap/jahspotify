package jahspotify.query;

import jahspotify.Query;
import junit.framework.TestCase;

import static jahspotify.Query.*;

/**
 * @author Johan Lindquist
 */
public class TestQuery extends TestCase
{

    public void testAndQuery() throws Exception
    {
        final Query and = not(and(artist("alika"), artist("Mad Professor")));
        final Query result = and.or(token("fred"));

        assertEquals("bad query", "(( NOT ((artist:\"alika\" AND artist:\"Mad Professor\"))) OR fred)", result.serialize());
    }

    public void testAndQueryWithTextQuery() throws Exception
    {
        final Query andQuery = artist("alika").and(text("some text"));

        assertEquals("bad query", "(artist:\"alika\" AND \"some text\")", andQuery.serialize());
    }
}
