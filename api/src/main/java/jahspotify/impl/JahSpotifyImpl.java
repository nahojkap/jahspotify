package jahspotify.impl;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
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

    private Stack<Library.Entry> _nodeStack = new Stack<Library.Entry>();
    private Library.Entry _currentLibraryEntry = new Library.Entry(null, "jahspotify:folder:ROOT", "ROOT", Link.Type.FOLDER.name());

    private Thread _jahSpotifyThread;
    private static JahSpotifyImpl _jahSpotify;
    private Library _library;
    private boolean _synching = false;
    private User _user;
    private AtomicInteger _globalToken = new AtomicInteger(1);

    private native int initialize(String username, String password);

    private final Set<Link> _lockedTracks = new CopyOnWriteArraySet<Link>();
    private final Set<Link> _lockedArtists = new CopyOnWriteArraySet<Link>();
    private final Set<Link> _lockedAlbums = new CopyOnWriteArraySet<Link>();
    private final Set<Link> _lockedImages = new CopyOnWriteArraySet<Link>();

    protected JahSpotifyImpl()
    {
        registerNativeMediaLoadedListener(new NativeMediaLoadedListener()
        {
            @Override
            public void track(final int token, final Link link)
            {
                _log.trace(String.format("Track loaded: token=%d link=%s", token, link));
                synchronized (_lockedTracks)
                {
                    if (_lockedTracks.contains(link))
                    {
                        _lockedTracks.remove(link);
                        _lockedTracks.notifyAll();
                    }
                }
            }

            @Override
            public void playlist(final int token, final Link link)
            {
                _log.trace(String.format("Playlist loaded: token=%d link=%s", token, link));
            }

            @Override
            public void album(final int token, final Link link)
            {
                _log.trace(String.format("Album loaded: token=%d link=%s", token, link));
                synchronized (_lockedAlbums)
                {
                    if (_lockedAlbums.contains(link))
                    {
                        _lockedAlbums.remove(link);
                        _lockedAlbums.notifyAll();
                    }
                }
            }

            @Override
            public void image(final int token, final Link link)
            {
                _log.trace(String.format("Image loaded: token=%d link=%s", token, link));
                synchronized (_lockedImages)
                {
                    if (_lockedImages.contains(link))
                    {
                        _lockedImages.remove(link);
                        _lockedImages.notifyAll();
                    }
                }
            }

            @Override
            public void artist(final int token, final Link link)
            {
                _log.trace(String.format("Artist loaded: token=%d link=%s", token, link));
                synchronized (_lockedArtists)
                {
                    if (_lockedArtists.contains(link))
                    {
                        _lockedArtists.remove(link);
                        _lockedArtists.notifyAll();
                    }
                }
            }
        });

        registerNativeSearchCompleteListener(new NativeSearchCompleteListener()
        {
            @Override
            public void searchCompleted(final int token, final SearchResult searchResult)
            {
                _log.debug(String.format("Search completed: token=%d searchResult=%s", token, searchResult));

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

                _library = new Library();
                _library.addEntry(_currentLibraryEntry);

                debugPrintNodes(_currentLibraryEntry, 0);

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.synchCompleted();
                }

                // FIXME: Spawn off thread which populates the nodes kept in-memory now
                _synching = false;
                _log.debug("Library ready");
            }

            @Override
            public void synchStarted(int numPlaylists)
            {
                _log.debug("Synch started: " + numPlaylists);
                _synching = true;
                _nodeStack.clear();
                _library = null;

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.synchStarted(numPlaylists);
                }
            }

            public void metadataUpdated(String uri)
            {
                final Link link = Link.create(uri);
                _log.debug("Metadata updated for '" + link + "', initiating reload of watched playlists");
                if (_synching)
                {
                    _log.debug("Metadata update received during synch - will ignore");
                    return;
                }

                // Should trawl the tree, from the root node down and update:
                // - folders
                // - playlists
                // - tracks
                // - albums

                for (PlaylistListener playlistListener : _playlistListeners)
                {
                    playlistListener.metadataUpdated(link);
                }

            }

            @Override
            public void startFolder(final String folderName, final long folderID)
            {
                _nodeStack.push(_currentLibraryEntry);
                final Link folderLink = Link.createFolderLink(folderID);
                _currentLibraryEntry = new Library.Entry(_currentLibraryEntry.getId(), folderLink.getId(), folderName, Link.Type.FOLDER.name());
                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.startFolder(folderLink, folderName);
                }
            }

            @Override
            public void endFolder()
            {
                final Library.Entry entry = _nodeStack.pop();
                entry.addSubEntry(_currentLibraryEntry);
                _currentLibraryEntry = entry;

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.endFolder(Link.create(entry.getId()));
                }
            }

            @Override
            public void playlist(final String name, final String link)
            {
                if (_synching)
                {
                    _currentLibraryEntry.addSubEntry(new Library.Entry(_currentLibraryEntry.getId(), link, name, Link.Type.PLAYLIST.name()));
                }

                for (PlaylistListener listener : _playlistListeners)
                {
                    listener.playlist(Link.create(link), name);
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
                _log.debug("Disconnected");
                _connected = false;
                _loggedIn = false;
            }

            @Override
            public void loggedIn()
            {
                _log.debug("Logged in");
                _loggedIn = true;
                _connected = true;
                for (ConnectionListener listener : _connectionListeners)
                {
                    listener.loggedIn();
                }
            }

            @Override
            public void loggedOut()
            {
                _log.debug("Logged out");
                _loggedIn = false;
            }
        });
    }

    private void debugPrintNodes(final Library.Entry entry, int indentation)
    {
        if (_log.isDebugEnabled())
        {
            String msg = "";
            for (int i = 0; i < indentation; i++)
            {
                msg = msg + " ";
            }
            Link.Type type = Link.Type.valueOf(entry.getType());
            switch (type)
            {
                case FOLDER:
                    msg = msg + "-" + entry.getName() + "(" + entry.getId() + ")";
                    _log.debug(msg);
                    List<Library.Entry> children = entry.getSubEntries();
                    for (Library.Entry child : children)
                    {
                        debugPrintNodes(child, indentation + 2);
                    }
                    break;
                case PLAYLIST:
                    msg = msg + "* " + entry.getName() + "(" + entry.getId() + ")";
                    _log.debug(msg);
                    break;
            }
        }
    }

    public static JahSpotify getInstance()
    {
        if (_jahSpotify == null)
        {
            _jahSpotify = new JahSpotifyImpl();
        }
        return _jahSpotify;
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

    public Album readAlbum(final Link uri)
    {
        ensureLoggedIn();

        synchronized (_lockedAlbums)
        {
            int count = 0;
            while (_lockedAlbums.contains(uri) && ++count < 5)
            {
                try
                {
                    _lockedAlbums.wait(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (count == 5)
            {
                // This would be quite bad ...
                return null;
            }

        }

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
        ensureLoggedIn();

        synchronized (_lockedArtists)
        {
            int count = 0;
            while (_lockedAlbums.contains(uri) && ++count < 5)
            {
                try
                {
                    _lockedArtists.wait(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (count == 5)
            {
                // This would be quite bad ...
                return null;
            }

        }
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
        ensureLoggedIn();
        synchronized (_lockedTracks)
        {
            int count = 0;
            while (_lockedTracks.contains(uri) && ++count < 5)
            {
                try
                {
                    _lockedTracks.wait(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (count == 5)
            {
                // This would be quite bad ...
                return null;
            }

        }
        try
        {
            _libSpotifyLock.lock();
            final Track track = retrieveTrack(uri.asString());
            if (track == null)
            {
                _lockedTracks.add(uri);
            }
            else
            {
                synchronized (_lockedTracks)
                {
                    _lockedTracks.remove(uri);
                    _lockedTracks.notifyAll();
                }
            }
            return track;
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    @Override
    public Image readImage(Link uri)
    {
        ensureLoggedIn();

        // Ok, till i figure out a better way of preventing overload on the spotify loading threads
        // (of multiple requests of the same image) i will have to do with this hack
        synchronized (_lockedImages)
        {
            int count = 0;
            while (_lockedImages.contains(uri) && ++count < 5)
            {
                try
                {
                    _lockedImages.wait(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (count == 5)
            {
                // This would be quite bad ...
                return null;
            }

        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        _libSpotifyLock.lock();
        int len = -1;
        try
        {
            len = readImage(uri.asString(), outputStream);
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

            synchronized (_lockedImages)
            {
                _lockedImages.remove(uri);
                _lockedImages.notifyAll();
            }
            return new Image(uri, bytes);
        }
        else
        {
            _lockedImages.add(uri);
        }
        return null;
    }

    @Override
    public Playlist readPlaylist(Link uri, final int index, final int numEntries)
    {
        ensureLoggedIn();
        _libSpotifyLock.lock();
        try
        {
            final Playlist playlist = retrievePlaylist(uri.asString());
            if ((index == 0 && numEntries == 0) || playlist == null)
            {
                return playlist;
            }

            // Trim the playlist accordingly now
            return trimPlaylist(playlist, index, numEntries);

        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    private Playlist trimPlaylist(final Playlist playlist, final int index, final int numEntries)
    {
        Playlist trimmedPlaylist = new Playlist();
        trimmedPlaylist.setAuthor(playlist.getAuthor());
        trimmedPlaylist.setCollaborative(playlist.isCollaborative());
        trimmedPlaylist.setDescription(playlist.getDescription());
        trimmedPlaylist.setId(playlist.getId());
        trimmedPlaylist.setName(playlist.getName());
        trimmedPlaylist.setPicture(playlist.getPicture());
        trimmedPlaylist.setNumTracks(numEntries == 0 ? playlist.getNumTracks() : numEntries);
        trimmedPlaylist.setIndex(index);
        // FIXME: Trim this list
        trimmedPlaylist.setTracks(playlist.getTracks().subList(index, numEntries));
        return null;
    }

    @Override
    public void pause()
    {
        ensureLoggedIn();
        nativePause();
    }

    private native int nativePause();

    @Override
    public void resume()
    {
        ensureLoggedIn();
        nativeResume();
    }

    private native int nativeResume();

    @Override
    public void play(Link link)
    {
        ensureLoggedIn();
        nativePlayTrack(link.asString());
    }

    private void ensureLoggedIn()
    {
        if (!_loggedIn)
        {
            throw new IllegalStateException("Not logged in");
        }
    }

    @Override
    public User getUser()
    {
        ensureLoggedIn();

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
        ensureLoggedIn();

        Library library = retrieveLibrary();
        if (library == null)
        {
            return null;
        }

        if (uri.getFolderId() == 0)
        {
            final Library.Entry entry = trimToLevel(_currentLibraryEntry, 0, level);
            populateEmptyEntries(entry, 0, level);
            return entry;
        }

        for (Library.Entry entry : library.getEntries())
        {
            final Library.Entry folderEntry = findFolder(entry, uri);
            if (folderEntry != null)
            {
                final Library.Entry trimmedEntry = trimToLevel(folderEntry, 0, level);
                populateEmptyEntries(trimmedEntry, 0, level);
                return trimmedEntry;
            }
        }
        return null;
    }

    @Override
    public void seek(final int offset)
    {
        ensureLoggedIn();
        nativeTrackSeek(offset);
    }

    @Override
    public void shutdown()
    {
        ensureLoggedIn();
        nativeShutdown();
    }

    @Override
    public void setCurrentGain(final float gain)
    {
        setAudioGain(gain);
    }

    @Override
    public float getCurrentGain()
    {
        return getAudioGain();
    }

    private void populateEmptyEntries(final Library.Entry entry, int currentLevel, int requiredLevel)
    {
        final Link.Type type = Link.Type.valueOf(entry.getType());
        _log.debug("Populating entry: " + entry.getId());
        _log.debug("Current level: " + currentLevel);
        _log.debug("Required level: " + requiredLevel);

        switch (type)
        {
            case PLAYLIST:
                if (entry.getSubEntries() == null || entry.getSubEntries().isEmpty())
                {
                    _log.debug("Loading tracks for playlist " + entry.getId());
                    // load the playlist!
                    Playlist playlist = readPlaylist(Link.create(entry.getId()), 0, 0);
                    if (playlist != null)
                    {
                        // Always add the number of tracks/entries to the playlist
                        entry.setNumEntries(playlist.getNumTracks());

                        // However, if the required level is reached in terms of trimmin' simply return
                        if (currentLevel == requiredLevel)
                        {
                            _log.debug("Required level reached");
                            return;
                        }

                        _log.debug("Playlist loaded, adding " + playlist.getTracks().size() + " track(s)");
                        for (Link trackLink : playlist.getTracks())
                        {
                            final Track track = readTrack(trackLink);
                            if (track != null)
                            {
                                entry.addSubEntry(new Library.Entry(entry.getId(), trackLink.getId(), track.getTitle(), Link.Type.TRACK.name()));
                            }
                        }
                    }
                }

            case FOLDER:
            default:
                // However, if the required level is reached in terms of trimmin' simply return
                if (currentLevel == requiredLevel)
                {
                    _log.debug("Required level reached");
                    for (Library.Entry subEntry : entry.getSubEntries())
                    {
                        populateEmptyEntries(subEntry, 0, 0);
                    }
                    return;
                }
                else
                {
                    for (Library.Entry subEntry : entry.getSubEntries())
                    {
                        populateEmptyEntries(subEntry, currentLevel + 1, requiredLevel);
                    }
                }
                break;
        }


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
                final Library.Entry e = new Library.Entry(folderEntry.getId(), entry.getId(), entry.getName(), entry.getType());
                e.setNumEntries(entry.getNumEntries());
                strippedSubEntries.add(e);
            }
        }
        else
        {
            for (Library.Entry entry : folderEntry.getSubEntries())
            {
                strippedSubEntries.add(trimToLevel(entry, currentLevel + 1, level));
            }
        }

        final Library.Entry entry = new Library.Entry(folderEntry.getParentID(), folderEntry.getId(), folderEntry.getName(), folderEntry.getType());
        entry.setSubEntries(strippedSubEntries);
        entry.setNumEntries(folderEntry.getNumEntries());
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
            Library.Entry foundEntry = findFolder(entry, uri);
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
        return _library;
    }

    @Override
    public boolean isStarted()
    {
        return _jahSpotifyThread != null;
    }

    @Override
    public void stop()
    {
        ensureLoggedIn();
        nativeStopTrack();
    }

    public void initiateSearch(final Search search)
    {
        ensureLoggedIn();

        _libSpotifyLock.lock();
        try
        {
            NativeSearchParameters nativeSearchParameters = initializeFromSearch(search);
            // TODO: Register the lister for the specified token
            nativeInitiateSearch(0, nativeSearchParameters);
        }
        finally
        {
            _libSpotifyLock.unlock();
        }
    }

    public void initiateSearch(final Search search, final SearchListener searchListener)
    {
        ensureLoggedIn();

        _libSpotifyLock.lock();
        try
        {
            int token = _globalToken.getAndIncrement();
            NativeSearchParameters nativeSearchParameters = initializeFromSearch(search);
            _prioritySearchListeners.put(token, searchListener);
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

    private native boolean registerNativeMediaLoadedListener(final NativeMediaLoadedListener nativeMediaLoadedListener);

    private native int readImage(String uri, OutputStream outputStream);

    private native User retrieveUser();

    private native Album retrieveAlbum(String uri);

    private native Artist retrieveArtist(String uri);

    private native Track retrieveTrack(String uri);

    private native Playlist retrievePlaylist(String uri);

    private native int nativePlayTrack(String uri);

    private native void nativeStopTrack();

    private native void nativeTrackSeek(int offset);

    private native boolean registerNativeConnectionListener(final NativeConnectionListener nativeConnectionListener);

    private native boolean registerNativeSearchCompleteListener(final NativeSearchCompleteListener nativeSearchCompleteListener);

    private native boolean registerNativeLibraryListener(NativeLibraryListener nativeLibraryListener);

    private native boolean registerNativePlaybackListener(NativePlaybackListener nativePlaybackListener);

    private native boolean nativeShutdown();

    private native void nativeInitiateSearch(final int i, NativeSearchParameters token);

    private native void setAudioGain(float gain);

    private native float getAudioGain();
}
