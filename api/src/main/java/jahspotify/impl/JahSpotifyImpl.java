package jahspotify.impl;

import java.io.*;
import java.util.*;

import jahspotify.*;
import jahspotify.media.*;
import jahspotify.storage.JahStorage;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyImpl implements JahSpotify
{
    private Log _log = LogFactory.getLog(JahSpotify.class);

    private JahStorage _jahStorage;

    private boolean _loggedIn = false;

    private List<PlaybackListener> _playbackListeners = new ArrayList<PlaybackListener>();
    private List<ConnectionListener> _connectionListeners = new ArrayList<ConnectionListener>();
    private List<PlaylistListener> _playlistListeners = new ArrayList<PlaylistListener>();

    private PlaylistFolderNode _rootNode = new PlaylistFolderNode("ROOT_NODE", "ROOT_NODE");
    private Stack<PlaylistFolderNode> _nodeStack = new Stack<PlaylistFolderNode>();
    private PlaylistFolderNode _currentPlaylistFolderNode = _rootNode;

    private Thread _jahSpotifyThread;

    private static JahSpotifyImpl _jahSpotifyImpl;
    private Library _library;
    private boolean _synching = false;
    private User _user;

    private native int initialize(String username, String password);

    public void setJahStorage(final JahStorage jahStorage)
    {
        _jahStorage = jahStorage;
    }

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
        registerPlaylistListener(new AbstractPlaylistListener()
        {
            @Override
            public void synchCompleted()
            {
                _log.debug("Synch complete");

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
                _log.debug("Synch started: " + numPlaylists);
                _synching = true;
                _rootNode.clear();
                _nodeStack.clear();
                _library = null;
                _currentPlaylistFolderNode = _rootNode;

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.synchStarted(numPlaylists);
                }
            }

            public void metadataUpdated()
            {
                _log.debug("Metadata updated, initiating reload of watched playlists");
                if (_synching)
                {
                    _log.debug("Metadata update received during synch - will ignore");
                    return;
                }

                _library = null;

                // Should trawl the tree, from the root node down and update:
                // - folders
                // - playlists
                // - tracks
                // - albums

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
                else
                {
                    _library = null;
                }
                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.playlist(name, link);
                }
            }
        });
        registerConnectionListener(new AbstractConnectionListener()
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

    private native int readImage(String uri, OutputStream outputStream);

    private native User retrieveUser();

    private native Album retrieveAlbum(String uri);

    private native Artist retrieveArtist(String uri);

    private native Track retrieveTrack(String uri);

    private native Playlist retrievePlaylist(String uri);

    private native String[] getTracksForPlaylist(String uri);

    public Album readAlbum(final Link uri)
    {
        Album album;
        if (_jahStorage != null)
        {
            album = _jahStorage.readAlbum(uri);
            if (album != null)
            {
                _log.debug("Found album for " + uri + " in storage, will return that");
                return album;
            }
        }

        album = retrieveAlbum(uri.asString());
        if (_jahStorage != null && album != null)
        {
            _jahStorage.store(album);
        }
        return album;
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        Artist artist;
        if (_jahStorage != null)
        {
            artist = _jahStorage.readArtist(uri);
            if (artist != null)
            {
                _log.debug("Found artist for " + uri + " in storage, will return that");
                return artist;
            }
        }
        artist = retrieveArtist(uri.asString());
        if (_jahStorage != null && artist != null)
        {
            _jahStorage.store(artist);
        }
        return artist;
    }



    @Override
    public Track readTrack(Link uri)
    {
        Track track;
        if (_jahStorage != null)
        {
            track = _jahStorage.readTrack(uri);
            if (track != null)
            {
                _log.debug("Found track for " + uri + " in storage, will return that");
                return track;
            }
        }

        track = retrieveTrack(uri.asString());

        if (_jahStorage != null && track != null)
        {
            _jahStorage.store(track);
        }

        return track;
    }

    @Override
    public Image readImage(Link uri)
    {
        if (_jahStorage != null)
        {
            Image image = _jahStorage.readImage(uri);
            if (image != null)
            {
                _log.debug("Found image for " + uri + " in storage, will return that");
                return image;
            }
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = _jahSpotifyImpl.readImage(uri.asString(), outputStream);
        if (len != -1)
        {

            final byte[] bytes = outputStream.toByteArray();
            if (len != bytes.length)
            {
                throw new IllegalStateException("Number bytes reported written does not match length of bytes (" + len + " != " + bytes.length + ")");
            }
            Image image = new Image(uri, bytes);
            if (_jahStorage != null)
            {
                _jahStorage.store(image);
            }
            return image;
        }
        return null;
    }

    @Override
    public Playlist readPlaylist(Link uri)
    {
        return retrievePlaylist(uri.asString());
    }

    @Override
    public List<Track> readTracks(Link... uris)
    {
        return Collections.emptyList();
    }

    public List<Track> readTracks(List<Link> uris)
    {
        return Collections.emptyList();
    }
    private native Track[] nativeReadTracks(String[] uris);

    @Override
    public native int pause();

    @Override
    public native int resume();

    @Override
    public int play(Link uri)
    {
        return playTrack(uri.asString());
    }

    private native int playTrack(String uri);

    private native boolean registerConnectionListener(final ConnectionListener connectionListener);

    private native boolean registerPlaylistListener(PlaylistListener playlistListener);

    private native boolean registerPlaybackListener(PlaybackListener playbackListener);

    private native boolean shutdown();

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
                _library.setOwner(getUser().getDisplayName());
            }

            for (Node node : _rootNode.getChildren())
            {
                Library.Entry entry = null;
                if (node instanceof PlaylistNode)
                {
                    entry = createPlaylistFromNode((PlaylistNode) node);
                }
                else if (node instanceof PlaylistFolderNode)
                {
                    entry = createFolderFromNode((PlaylistFolderNode)node);
                }

                if (entry != null)
                {
                    _library.addEntry(entry);
                }


            }



        }

        return _library;
    }

    private Library.Entry createFolderFromNode(final PlaylistFolderNode playlistFolderNode)
    {
        Library.Entry folder = Library.Entry.createFolderEntry(playlistFolderNode.getID(),playlistFolderNode.getName());

        for (Node node : playlistFolderNode.getChildren())
        {
            if (node instanceof PlaylistNode)
            {
                Library.Entry playlist = createPlaylistFromNode((PlaylistNode) node);
                folder.addSubEntry(playlist);
            }
            else if (node instanceof PlaylistFolderNode)
            {
                final Library.Entry folderFromNode = createFolderFromNode((PlaylistFolderNode) node);
                folder.addSubEntry(folderFromNode);
            }
        }

        return folder;
    }

    private Library.Entry createPlaylistFromNode(final PlaylistNode playlistNode)
    {
        final Library.Entry playlistEntry = Library.Entry.createPlaylistEntry(playlistNode.getID(),playlistNode.getName());
        Playlist playlist = playlistNode.getPlaylist();
        if (playlist == null)
        {
            playlist = retrievePlaylist(playlistNode.getID());

            playlistNode.setPlaylist(playlist);

            for (Link trackLink : playlist.getTracks())
            {
                Track track = readTrack(trackLink);
                TrackNode trackNode = new TrackNode(track.getId().toString(),track.getTitle());
                trackNode.setTrack(track);
                playlistNode.addTrackNode(trackNode);
                playlistEntry.addSubEntry(Library.Entry.createTrackEntry(track.getId().toString(),track.getTitle()));
            }
        }
        else
        {
            // If it is already loaded that is ...
        }

        return playlistEntry;
    }

    @Override
    public boolean isStarted()
    {
        return _jahSpotifyThread != null;
    }

    @Override
    public void stop()
    {
        shutdown();
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
