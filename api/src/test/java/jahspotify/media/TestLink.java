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

        link = link.create("jahspotify:podcast:http://www.localhost/");
        assertNotNull("no link created", link);
        assertEquals("bad type", Link.Type.PODCAST,link.getType());
        assertEquals("bad uri","http://www.localhost/",link.getUri());

        link = link.create("jahspotify:mp3:http://www.localhost/");
        assertNotNull("no link created", link);
        assertEquals("bad type", Link.Type.MP3,link.getType());
        assertEquals("bad uri","http://www.localhost/",link.getUri());

    }
}
