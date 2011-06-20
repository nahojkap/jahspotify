package jahspotify.media;

import junit.framework.TestCase;

/**
 * @author Johan Lindquist
 */
public class TestLink extends TestCase
{

    public void testCreate() throws Exception
    {
        Link link = Link.create("spotify:album:3PogVmhNucYNfyywZvTd7F");
        assertNotNull("no link created", link);
        assertEquals("bad type", Link.Type.ALBUM,link.getType());

        link = Link.create("spotify:track:3PogVmhNucYNfyywZvTd7F");
        assertNotNull("no link created", link);
        assertEquals("bad type", Link.Type.TRACK,link.getType());

        link = Link.create("jahspotify:queue:default");
        assertNotNull("no link created", link);
        assertEquals("bad type", Link.Type.QUEUE,link.getType());
        assertEquals("bad stuff", "default",link.getQueue());


    }
}
