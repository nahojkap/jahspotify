package jahspotify.impl;

import java.io.*;
import java.util.*;
import java.util.List;

import jahspotify.*;
import jahspotify.media.*;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyImpl implements JahSpotify
{
    private Log _log = LogFactory.getLog(JahSpotify.class);

    private boolean _loggedIn = false;

    private List<PlaybackListener> _playbackListeners = new ArrayList<PlaybackListener>();
    private List<ConnectionListener> _connectionListeners = new ArrayList<ConnectionListener>();
    private List<PlaylistListener> _playlistListeners = new ArrayList<PlaylistListener>();

    private PlaylistFolderNode _rootNode = new PlaylistFolderNode("ROOT_NODE", "ROOT_NODE");
    private Stack<PlaylistFolderNode> _nodeStack = new Stack<PlaylistFolderNode>();
    private PlaylistFolderNode _currentPlaylistFolderNode = _rootNode;

    private Map<String, Track> _trackCache = new HashMap<String, Track>();

    private Thread _jahSpotifyThread;

    private static JahSpotifyImpl _jahSpotifyImpl;
    private Library _library;
    private boolean _synching = false;
    private User _user;

    private native int initialize(String username, String password);

    private JahSpotifyImpl()
    {
        registerPlaybackListener(new PlaybackListener()
        {
            @Override
            public void trackStarted(final String uri)
            {
                for (PlaybackListener listener : _playbackListeners)
                {
                    listener.trackStarted(uri);
                }
            }

            @Override
            public void trackEnded(final String uri, final boolean forcedEnd)
            {
                for (PlaybackListener listener : _playbackListeners)
                {
                    listener.trackEnded(uri, forcedEnd);
                }

            }

            @Override
            public String nextTrackToPreload()
            {
                for (PlaybackListener listener : _playbackListeners)
                {
                    String nextTrack = listener.nextTrackToPreload();
                    if (nextTrack != null)
                    {
                        return nextTrack;
                    }
                }
                return null;
            }
        });
        registerPlaylistListener(new PlaylistListener()
        {
            @Override
            public void synchCompleted()
            {
                if (!_nodeStack.isEmpty())
                {
                    // Something is wrong
                    _log.warn("Node stack is not empty, yet we received synch completed");
                }

                if (_log.isDebugEnabled())
                {
                    debugPrintNodes(_rootNode,0);
                }

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.synchCompleted();
                }

                // FIXME: Spawn off thread which populates the nodes kept in-memory now

                _synching = false;

            }

            @Override
            public void synchStarted(int numPlaylists)
            {
                _synching = true;
                _rootNode.clear();
                _nodeStack.clear();
                _currentPlaylistFolderNode = _rootNode;

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.synchStarted(numPlaylists);
                }
            }

            @Override
            public void startFolder(final String folderName, final long folderID)
            {
                _nodeStack.push(_currentPlaylistFolderNode);
                _currentPlaylistFolderNode = new PlaylistFolderNode(Long.toString(folderID),folderName);

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.startFolder(folderName, folderID);
                }
            }

            @Override
            public void endFolder()
            {
                PlaylistFolderNode playlistFolderNode = _nodeStack.pop();
                playlistFolderNode.addChild(_currentPlaylistFolderNode);
                _currentPlaylistFolderNode = playlistFolderNode;

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.endFolder();
                }
            }

            @Override
            public void playlist(final String name, final String link)
            {
                if (_synching)
                {
                    _currentPlaylistFolderNode.addChild(new PlaylistNode(link,name));
                }

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.playlist(name, link);
                }
            }
        });
        registerConnectionListener(new ConnectionListener()
        {
            @Override
            public void connected()
            {
                for (ConnectionListener listener : _connectionListeners)
                {
                    listener.connected();
                }
            }

            @Override
            public void loggedIn()
            {
                _loggedIn = true;
                for (ConnectionListener listener : _connectionListeners)
                {
                    listener.loggedIn();
                }
            }
        });
    }

    private void debugPrintNodes(final Node node,int indentation)
    {
        String msg = "";
        for (int i = 0; i < indentation; i++)
        {
            msg = msg + " ";
        }
        if (node instanceof PlaylistFolderNode)
        {
            msg = msg + "-" + node._name + "(" + node._id + ")";
            _log.debug(msg);
            List<Node> children = ((PlaylistFolderNode)node).getChildren();
            for (Node child : children)
            {
                debugPrintNodes(child, indentation+2);
            }
        }
        else if (node instanceof PlaylistNode)
        {
            msg = msg + "* " + node._name + "(" + node._id + ")";
            _log.debug(msg);
        }

    }

    public static JahSpotifyImpl getInstance()
    {
        if (_jahSpotifyImpl == null)
        {
            _jahSpotifyImpl = new JahSpotifyImpl();
        }
        return _jahSpotifyImpl;
    }

    @Override
    public void start(final String username, final String password)
    {
        if (_jahSpotifyThread != null)
        {
            return;
        }
        _jahSpotifyThread = new Thread()
        {
            @Override
            public void run()
            {
                initialize(username, password);
            }
        };
        _jahSpotifyThread.start();
    }

    @Override
    public native int readImage(String uri, OutputStream outputStream);

    private native User retrieveUser();

    private native Album retrieveAlbum(String uri);

    private native Track retrieveTrack(String uri);

    private native Playlist retrievePlaylist(String uri);

    private native boolean populatePlaylist(String uri, Playlist playlist);

    private native String[] getTracksForPlaylist(String uri);

    @Override
    public Album readAlbum(final String uri)
    {
        return retrieveAlbum(uri);
    }

    @Override
    public Track readTrack(String uri)
    {
        return retrieveTrack(uri);
    }

    @Override
    public byte[] readImage(String uri)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        _jahSpotifyImpl.readImage(uri, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public Playlist readPlaylist(String uri)
    {
        return retrievePlaylist(uri);
    }

    @Override
    public List<Track> readTracks(String... uris)
    {
        return Collections.emptyList();
    }

    private native Track[] nativeReadTracks(String[] uris);

    @Override
    public native int pause();

    @Override
    public native int resume();

    @Override
    public native int play(String uri);

    private native boolean registerConnectionListener(final ConnectionListener connectionListener);

    private native boolean registerPlaylistListener(PlaylistListener playlistListener);

    private native boolean registerPlaybackListener(PlaybackListener playbackListener);


    @Override
    public User getUser()
    {
        if (!_loggedIn)
        {
            throw new IllegalStateException("Not Logged In");
        }

        if (_user != null)
        {
            return _user;
        }

        _user = retrieveUser();

        return _user;
    }

    static
    {
        System.loadLibrary("jahspotify");
        System.loadLibrary("spotify");
    }

    @Override
    public void addPlaybackListener(final PlaybackListener playbackListener)
    {
        _playbackListeners.add(playbackListener);
    }

    @Override
    public void addPlaylistListener(final PlaylistListener playlistListener)
    {
        _playlistListeners.add(playlistListener);
    }

    @Override
    public void addConnectionListener(final ConnectionListener connectionListener)
    {
        _connectionListeners.add(connectionListener);
    }

    public Library retrieveLibrary()
    {

        while (_synching)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if (_library == null)
        {
            _library = new Library();
            if (getUser() != null)
            {
                _library.setAuthor(getUser().getDisplayName());
            }

            for (Node node : _rootNode.getChildren())
            {
                Container container = null;
                if (node instanceof PlaylistNode)
                {
                    container = createPlaylistFromNode((PlaylistNode) node);
                }
                else if (node instanceof PlaylistFolderNode)
                {
                    container = createFolderFromNode((PlaylistFolderNode)node);
                }

                if (container != null)
                {
                    _library.addChild(container);
                }


            }



        }

        return _library;
    }

    private Container createFolderFromNode(final PlaylistFolderNode playlistFolderNode)
    {
        Folder folder = new Folder();
        folder.setName(playlistFolderNode.getName());

        for (Node node : playlistFolderNode.getChildren())
        {
            if (node instanceof PlaylistNode)
            {
                Playlist playlist = createPlaylistFromNode((PlaylistNode) node);
                folder.addChild(playlist);
            }
            else if (node instanceof PlaylistFolderNode)
            {
                final Container folderFromNode = createFolderFromNode((PlaylistFolderNode) node);
                folder.addChild(folderFromNode);
            }
        }

        return folder;
    }

    private Playlist createPlaylistFromNode(final PlaylistNode playlistNode)
    {
        Playlist playlist = playlistNode.getPlaylist();
        if (playlist == null)
        {
            playlist = retrievePlaylist(playlistNode.getID());

            playlistNode.setPlaylist(playlist);

            for (Track track : playlist.getTracks())
            {
                TrackNode trackNode = new TrackNode(track.getId(),track.getTitle());
                trackNode.setTrack(track);
                playlistNode.addTrackNode(trackNode);
            }

        }

        for (TrackNode trackNode : playlistNode.getTracks())
        {

        }
        return playlist;
    }

    @Override
    public boolean isStarted()
    {
        return _jahSpotifyThread != null;
    }

    @Override
    public void stop()
    {
        // FIXME: should shutdown threads and close the connection now
    }

    private static class Node
    {
        private Node(final String id, final String name)
        {
            _id = id;
            _name = name;
        }

        String _name;
        String _id;


        public String getID()
        {
            return _id;
        }

        public String getName()
        {
            return _name;
        }

        @Override
        public String toString()
        {
            return "Node{" +
                    "_id='" + _id + '\'' +
                    ", _name='" + _name + '\'' +
                    '}';
        }
    }

    private static class PlaylistFolderNode extends Node
    {
        private PlaylistFolderNode(String id, String name)
        {
            super(id, name);
        }

        List<Node> _children = new ArrayList<Node>();

        public void addChild(Node node)
        {
            if (node instanceof PlaylistNode || node instanceof PlaylistFolderNode)
            {
                _children.add(node);
            }
            else
            {
                throw new IllegalArgumentException("Only PlaylistNode or PlaylistFolderNode nodes are allowed");
            }
        }

        public List<Node> getChildren()
        {
            return _children;
        }

        public void clear()
        {
            _children.clear();
        }

        @Override
        public String toString()
        {
            return "PlaylistFolderNode{" +
                    "_children=" + _children +
                    "} " + super.toString();
        }
    }

    private static class PlaylistNode extends Node
    {
        private PlaylistNode(String id, String name)
        {
            super(id, name);
        }

        Playlist _playlist;
        List<TrackNode> _tracks = new ArrayList<TrackNode>();

        public Playlist getPlaylist()
        {
            return _playlist;
        }

        public void setPlaylist(final Playlist playlist)
        {
            _playlist = playlist;
        }

        public List<TrackNode> getTracks()
        {
            return _tracks;
        }

        public void addTrackNode(TrackNode track)
        {
            _tracks.add(track);

        }

        public void setTracks(final List<TrackNode> tracks)
        {
            _tracks = tracks;
        }

        @Override
        public String toString()
        {
            return "PlaylistNode{" +
                    "_playlist=" + _playlist +
                    "} " + super.toString();
        }
    }

    private static class TrackNode extends Node
    {
        Track _track;

        private TrackNode(final String id, final String name)
        {
            super(id, name);
        }

        public Track getTrack()
        {
            return _track;
        }

        public void setTrack(final Track track)
        {
            _track = track;
        }

        @Override
        public String toString()
        {
            return "TrackNode{" +
                    "_track=" + _track +
                    "} " + super.toString();
        }
    }
}
