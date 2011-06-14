import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import jahspotify.*;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.User;
import junit.framework.*;

public class JahSpotifyTest extends TestCase
{
    private String _lastURI;

    public JahSpotifyTest(String name)
    {
        super(name);
    }

    protected void setUp()
            throws Exception
    {
        super.setUp();
    }

    protected void tearDown()
            throws Exception
    {
        super.tearDown();
    }

    static int indent = 0;

    public void testNativeHelloWorld()
            throws Exception
    {


        final JahSpotifyImpl app = JahSpotifyImpl.getInstance();

        app.addConnectionListener(new ConnectionListener()
        {
            @Override
            public void connected()
            {
                System.out.println("Connected!");
            }

            @Override
            public void loggedIn()
            {

                System.out.println("Logged in!");

                final User user = app.getUser();
                if (user != null)
                {
                    System.out.println(user);
                }
                else
                {
                    System.out.println("No user received");
                }
            }
        });

        final List<String> _uri = new ArrayList<String>();
        final AtomicBoolean _synching = new AtomicBoolean(false);

        app.addPlaybackListener(new PlaybackListener()
        {
            @Override
            public void trackStarted(final String uri)
            {
                System.out.println("Track started: " + uri);
            }

            @Override
            public void trackEnded(final String uri, final boolean forcedEnd)
            {
                System.out.println("Track ended: " + uri + (forcedEnd ? " (forced)" : ""));
            }

            @Override
            public String nextTrackToPreload()
            {
                return null;
            }
        });

        app.addPlaylistListener(new PlaylistListener()
        {
            @Override
            public void synchStarted(final int numPlaylists)
            {
                System.out.println("Synch starting: " + numPlaylists);
                _synching.set(true);
            }

            @Override
            public void synchCompleted()
            {
                _synching.set(false);
                System.out.println("Synch completed, captured URIs");
                // app.readPlaylist("spotify:user:jlindquist:playlist:1qaBfPUxqDrcdHNhhzTq8w");
            }

            @Override
            public void startFolder(final String folderName, final long folderID)
            {
                for (int i = 0 ; i < indent; i++)
                {
                    System.out.print(" ");
                }
                indent++;
                System.out.println("+" + folderName);
            }

            @Override
            public void endFolder()
            {
                indent--;
            }

            @Override
            public void playlist(final String name, final String link)
            {
                if (_synching.get())
                {
                for (int i = 0 ; i < indent; i++)
                {
                    System.out.print(" ");
                }
                System.out.println("-" + name);
                _lastURI = link;
                }
            }
        });
        try
        {
            app.start(System.getProperty("spotify.username"),System.getProperty("spotify.password"));

            Thread.sleep(5000);

            app.play("spotify:track:6KEp0gCOU3frnsaxb0Gq3d");

            Thread.sleep(10000);
            app.play("spotify:track:46tnXk2k4uoy0skIcC69Fy");
            // System.out.println("t = " + t);


            while(true)
            {
                Thread.sleep(1000);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}

