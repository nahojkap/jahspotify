package jahspotify.impl;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import jahspotify.*;
import jahspotify.media.*;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyImpl implements JahSpotify
{
    private Log _log = LogFactory.getLog(JahSpotify.class);

    private Lock _libSpotifyLock = new ReentrantLock();

    private boolean _loggedIn = false;
    private boolean _connected;

    private List<PlaybackListener> _playbackListeners = new ArrayList<PlaybackListener>();
    private List<ConnectionListener> _connectionListeners = new ArrayList<ConnectionListener>();

    private List<SearchListener> _searchListeners = new ArrayList<SearchListener>();
    private Map<Integer, SearchListener> _prioritySearchListeners = new HashMap<Integer, SearchListener>();

    private List<PlaylistListener> _playlistListeners = new ArrayList<PlaylistListener>();
    private PlaylistFolderNode _rootNode = new PlaylistFolderNode("ROOT_NODE", "ROOT_NODE");
    private Stack<PlaylistFolderNode> _nodeStack = new Stack<PlaylistFolderNode>();

    private PlaylistFolderNode _currentPlaylistFolderNode = _rootNode;

    private Thread _jahSpotifyThread;
    private static JahSpotifyImpl _jahSpotifyImpl;
    private Library _library;
    private boolean _synching = false;
    private User _user;
    private AtomicInteger _globalToken = new AtomicInteger(1);

    private native int initialize(String username, String password);

    protected JahSpotifyImpl()
    {
        registerNativeMediaLoadedListener(new NativeMediaLoadedListener()
        {
            @Override
            public void track(final int token,final Link link)
            {
            }

            @Override
            public void playlist(final int token, final Link link)
            {
            }

            @Override
            public void album(final int token,final Link link)
            {
            }

            @Override
            public void image(final int token, final Link link)
            {
            }

            @Override
            public void artist(final int token,final Link link)
            {
            }
        });

        registerNativeSearchCompleteListener(new NativeSearchCompleteListener()
        {
            @Override
            public void searchCompleted(final int token, final SearchResult searchResult)
            {
                if (token > 0)
                {
                    final SearchListener searchListener = _prioritySearchListeners.get(token);
                    if (searchListener != null)
                    {
                        searchListener.searchComplete(searchResult);
                    }
                }
                for (SearchListener searchListener : _searchListeners)
                {
                    searchListener.searchComplete(searchResult);
                }
            }
        });

        registerNativePlaybackListener(new NativePlaybackListener()
        {
            @Override
            public void trackStarted(final String uri)
            {
                _log.debug("Track started: " + uri);
                for (PlaybackListener listener : _playbackListeners)
                {
                    listener.trackStarted(Link.create(uri));
                }
            }

            @Override
            public void trackEnded(final String uri, final boolean forcedEnd)
            {
                _log.debug("Track ended signalled: " + uri + " (" + (forcedEnd ? "forced)" : "natural ending)"));
                for (PlaybackListener listener : _playbackListeners)
                {
                    listener.trackEnded(Link.create(uri), forcedEnd);
                }

            }

            @Override
            public String nextTrackToPreload()
            {
                _log.debug("Next to pre-load, will query listeners");
                for (PlaybackListener listener : _playbackListeners)
                {
                    Link nextTrack = listener.nextTrackToPreload();
                    if (nextTrack != null)
                    {
                        _log.debug("Listener returned non-null value: " + nextTrack);
                        return nextTrack.asString();
                    }
                }
                return null;
            }
        });
        registerNativeLibraryListener(new NativeLibraryListener()
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
                    debugPrintNodes(_rootNode, 0);
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

            public void metadataUpdated(String link)
            {
                _log.debug("Metadata updated for '" + link + "', initiating reload of watched playlists");
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
                final Link folderLink = Link.createFolderLink(folderID);
                _currentPlaylistFolderNode = new PlaylistFolderNode(folderLink.getId(), folderName);

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.startFolder(folderLink, folderName);
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
                    listener.endFolder(Link.create(playlistFolderNode.getID()));
                }
            }

            @Override
            public void playlist(final String name, final String link)
            {
                if (_synching)
                {
                    _currentPlaylistFolderNode.addChild(new PlaylistNode(link, name));
                }
                else
                {
                    _library = null;
                }
                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.playlist(Link.create(link),name);
                }
            }
        });
        registerNativeConnectionListener(new NativeConnectionListener()
        {
            @Override
            public void connected()
            {
                _connected = true;
                for (ConnectionListener listener : _connectionListeners)
                {
                    listener.connected();
                }
            }

            @Override
            public void disconnected()
            {
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

            @Override
            public void loggedOut()
            {
            }
        });
    }

    private void debugPrintNodes(final Node node, int indentation)
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
            List<Node> children = ((PlaylistFolderNode) node).getChildren();
            for (Node child : children)
            {
                debugPrintNodes(child, indentation + 2);
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
    public void login(final String username, final String password)
    {
        _libSpotifyLock.lock();
        try
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
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    private native boolean registerNativeMediaLoadedListener(final NativeMediaLoadedListener nativeMediaLoadedListener);

    private native int readImage(String uri, OutputStream outputStream);

    private native User retrieveUser();

    private native Album retrieveAlbum(String uri);

    private native Artist retrieveArtist(String uri);

    private native Track retrieveTrack(String uri);

    private native Playlist retrievePlaylist(String uri);

    public Album readAlbum(final Link uri)
    {
        _libSpotifyLock.lock();
        try
        {
            return retrieveAlbum(uri.asString());
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        try
        {
            _libSpotifyLock.lock();
            return retrieveArtist(uri.asString());
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }


    @Override
    public Track readTrack(Link uri)
    {
        try
        {
            _libSpotifyLock.lock();
            return retrieveTrack(uri.asString());
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    @Override
    public Image readImage(Link uri)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        _libSpotifyLock.lock();
        int len = -1;
        try
        {
            len = _jahSpotifyImpl.readImage(uri.asString(), outputStream);
        }
        finally
        {
            _libSpotifyLock.unlock();
        }

        if (len > 0)
        {
            final byte[] bytes = outputStream.toByteArray();
            if (len != bytes.length)
            {
                throw new IllegalStateException("Number bytes reported written does not match length of bytes (" + len + " != " + bytes.length + ")");
            }
            return new Image(uri, bytes);
        }
        return null;
    }

    @Override
    public Playlist readPlaylist(Link uri)
    {
        _libSpotifyLock.lock();
        try
        {
            return retrievePlaylist(uri.asString());
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    @Override
    public void pause()
    {
        nativePause();
    }

    private native int nativePause();

    @Override
    public void resume()
    {
        nativeResume();
    }

    private native int nativeResume();

    @Override
    public void play(Link link)
    {
        nativePlayTrack(link.asString());
    }

    private native int nativePlayTrack(String uri);

    private native boolean registerNativeConnectionListener(final NativeConnectionListener nativeConnectionListener);

    private native boolean registerNativeSearchCompleteListener(final NativeSearchCompleteListener nativeSearchCompleteListener);

    private native boolean registerNativeLibraryListener(NativeLibraryListener nativeLibraryListener);

    private native boolean registerNativePlaybackListener(NativePlaybackListener nativePlaybackListener);

    private native boolean shutdown();

    private native void nativeInitiateSearch(final int i, NativeSearchParameters token);

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

    @Override
    public void addSearchListener(final SearchListener searchListener)
    {
        _searchListeners.add(searchListener);
    }

    @Override
    public Library.Entry readFolder(final Link uri, final int level)
    {
        Library library = retrieveLibrary();
        if (library == null)
        {
            return null;
        }

        if (uri.getFolderId() == 0)
        {
            Library.Entry rootEntry = new Library.Entry("jahspotify:folder:ROOT","ROOT",Library.Entry.FOLDER_ENTRY_TYPE);
            rootEntry.setSubEntries(library.getEntries());
            return trimToLevel(rootEntry,1,level);
        }

        for (Library.Entry entry : library.getEntries())
        {
            Library.Entry folderEntry = findFolder(entry, uri);
            if (folderEntry != null)
            {
                return trimToLevel(folderEntry,1,level);
            }
        }
        return null;
    }

    private Library.Entry trimToLevel(final Library.Entry folderEntry, final int currentLevel, final int level)
    {
        if (level == 0)
        {
            return folderEntry;
        }

        final List<Library.Entry> strippedSubEntries = new ArrayList<Library.Entry>();
        if (level == currentLevel)
        {
            // Remove all children of any sub-entries now
            for (Library.Entry entry : folderEntry.getSubEntries())
            {
                strippedSubEntries.add(new Library.Entry(entry.getId(),entry.getName(), entry.getType()));
            }
        }
        else
        {
            for (Library.Entry entry : folderEntry.getSubEntries())
            {
                strippedSubEntries.add(trimToLevel(entry,currentLevel+1,level));
            }
        }

        final Library.Entry entry = new Library.Entry(folderEntry.getId(), folderEntry.getName(), folderEntry.getType());
        entry.setSubEntries(strippedSubEntries);
        return entry;
    }

    private Library.Entry findFolder(final Library.Entry rootEntry, final Link uri)
    {
        if (isFolderMatch(rootEntry, uri))
        {
            return rootEntry;
        }
        for (Library.Entry entry : rootEntry.getSubEntries())
        {
            Library.Entry foundEntry = findFolder(entry,uri);
            if (foundEntry != null)
            {
                return foundEntry;
            }
        }
        return null;
    }

    private boolean isFolderMatch(final Library.Entry rootEntry, final Link uri)
    {
        if (rootEntry.getType().equals(Library.Entry.FOLDER_ENTRY_TYPE))
        {
            if (rootEntry.getId().equals(uri.getId()))
            {
                return true;
            }
        }
        return false;
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
            Library library = new Library();
            if (getUser() != null)
            {
                library.setOwner(getUser().getDisplayName());
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
                    entry = createFolderFromNode((PlaylistFolderNode) node);
                }

                if (entry != null)
                {
                    library.addEntry(entry);
                }
            }
            _library = library;


        }

        return _library;
    }

    private Library.Entry createFolderFromNode(final PlaylistFolderNode playlistFolderNode)
    {
        Library.Entry folder = Library.Entry.createFolderEntry(playlistFolderNode.getID(), playlistFolderNode.getName());

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
        final Library.Entry playlistEntry = Library.Entry.createPlaylistEntry(playlistNode.getID(), playlistNode.getName());
        Playlist playlist = playlistNode.getPlaylist();
        if (playlist == null)
        {
            playlist = retrievePlaylist(playlistNode.getID());

            if (playlist != null)
            {
                playlistNode.setPlaylist(playlist);
                for (Link trackLink : playlist.getTracks())
                {
                    Track track = readTrack(trackLink);
                    TrackNode trackNode = new TrackNode(track.getId().toString(), track.getTitle());
                    trackNode.setTrack(track);
                    playlistNode.addTrackNode(trackNode);
                    playlistEntry.addSubEntry(Library.Entry.createTrackEntry(track.getId().toString(), track.getTitle()));
                }
            }
            else
            {
                _log.debug("Could not load playlist for: " + playlistNode.getID());
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

    public void initiateSearch(final Search search)
    {
        _libSpotifyLock.lock();
        try
        {
            NativeSearchParameters nativeSearchParameters = initializeFromSearch(search);
            // TODO: Register the lister for the specified token
            nativeInitiateSearch(0,nativeSearchParameters);
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    public void initiateSearch(final Search search, final SearchListener searchListener)
    {
        _libSpotifyLock.lock();
        try
        {
            int token = _globalToken.getAndIncrement();
            NativeSearchParameters nativeSearchParameters = initializeFromSearch(search);
            _prioritySearchListeners.put(token,searchListener);
            nativeInitiateSearch(token, nativeSearchParameters);
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    public NativeSearchParameters initializeFromSearch(Search search)
    {
        NativeSearchParameters nativeSearchParameters = new NativeSearchParameters();
        nativeSearchParameters._query = search.getQuery().serialize();
        nativeSearchParameters.albumOffset = search.getAlbumOffset();
        nativeSearchParameters.artistOffset = search.getArtistOffset();
        nativeSearchParameters.trackOffset = search.getTrackOffset();
        nativeSearchParameters.numAlbums = search.getNumAlbums();
        nativeSearchParameters.numArtists = search.getNumArtists();
        nativeSearchParameters.numTracks = search.getNumTracks();
        return nativeSearchParameters;
    }

    public static class NativeSearchParameters
    {
        String _query;

        int trackOffset = 0;
        int numTracks = 255;

        int albumOffset = 0;
        int numAlbums = 255;

        int artistOffset = 0;
        int numArtists = 255;


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
