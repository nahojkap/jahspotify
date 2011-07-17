package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import javax.annotation.PostConstruct;

import jahspotify.*;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Lazy(false)
public class QueueManager
{
    private Log _log = LogFactory.getLog(QueueManager.class);

    private int _maxQueueSize;
    private int _numTracksPlayed;
    private int _numTracksSkipped;
    private int _totalPlayTime;

    private long _currentTrackStart;

    private BlockingDeque<QueueTrack> _uriQueue = new LinkedBlockingDeque<QueueTrack>();


    private boolean _repeatCurrentQueue;
    private boolean _repeatCurrentTrack;
    private boolean _shuffle;

    private Lock _queueLock = new ReentrantLock();

    // Holder for our 'shuffle' information - this is reset whenever the status of the 'shuffle' flag is changed
    private Map<Link, Integer> _shuffleTracker = new HashMap<Link, Integer>();

    private QueueTrack _currentTrack = null;
    private JahSpotify _jahSpotify;

    private static final Link DEFAULT_QUEUE_LINK = Link.create("jahspotify:queue:default");

    private Set<QueueListener> _queueListeners = new TreeSet<QueueListener>();

    @Autowired
    private MediaPlayer _mediaPlayer;

    @PostConstruct
    public void initialize()
    {
        _jahSpotify = JahSpotifyImpl.getInstance();

        _mediaPlayer.addMediaPlayerListener(new MediaPlayerListener()
        {
            @Override
            public void trackStart(final QueueTrack queueTrack)
            {
                _log.debug("Track start signalled: " + queueTrack);
                _currentTrackStart = System.currentTimeMillis();
                _currentTrack = queueTrack;
            }

            @Override
            public void trackEnd(final QueueTrack queueTrack, boolean forcedEnd)
            {
                try
                {
                    final Link trackEnded = queueTrack.getTrackUri();

                    _numTracksPlayed++;
                    _totalPlayTime += ((System.currentTimeMillis() - _currentTrackStart) / 1000);
                    if (forcedEnd)
                    {
                        _numTracksSkipped++;
                    }

                    _log.debug("End of track: " + trackEnded + (forcedEnd ? " (forced)" : ""));
                    if (!forcedEnd)
                    {
                        if (_currentTrack == null)
                        {
                            // Hmm
                            _log.debug("Current track is already null!");
                            return;
                        }

                        if (!trackEnded.equals(_currentTrack.getTrackUri()))
                        {
                            _log.debug("Current track don't match what is ending: " + trackEnded + " != " + _currentTrack.getTrackUri());
                            return;
                        }

                        // Push the current track back onto the uri queue if we are repeating it
                        if (_repeatCurrentTrack)
                        {
                            _log.debug("Current track is being repeated, placing at the front of the queue: " + _currentTrack);
                            _uriQueue.putFirst(_currentTrack);
                        }
                        // If we are repeating the whole queue simply dump it back onto the back of the queue
                        else if (_repeatCurrentQueue)
                        {
                            _log.debug("Current queue is being repeated, placing at the back of the queue: " + _currentTrack);
                            _uriQueue.add(_currentTrack);
                        }

                        _currentTrack = null;

                    }
                }
                catch (Exception e)
                {
                    _log.error("Error handling track ended callback: " + e.getMessage(), e);
                }
            }

            public QueueTrack nextTrackToQueue()
            {
                if (_repeatCurrentTrack)
                {
                    return null;
                }

                final QueueTrack peek = _uriQueue.peek();
                if (peek != null)
                {
                    return peek;
                }
                return null;
            }

            @Override
            public void paused(final QueueTrack currentTrack)
            {
            }

            @Override
            public void resume(final QueueTrack currentTrack)
            {
            }

            @Override
            public void skip(final QueueTrack currentTrack, final QueueTrack nextTrack)
            {
            }

        });

    }

    /**
     *
     * @param uris
     * @return
     */
    public CurrentQueue addToQueue(List<Link> uris)
    {
        final List<Link> allURIs = new ArrayList<Link>();

        for (final Link uri : uris)
        {
            if (uri.isPlaylistLink())
            {
                _log.debug("Received playlist, processing for tracks");
                // Have playlist
                Playlist playlist = _jahSpotify.readPlaylist(uri);
                if (playlist != null)
                {
                    for (final Link track : playlist.getTracks())
                    {
                        allURIs.add(track);
                    }
                    _log.debug("Playlist processed ...");
                }
                else
                {
                    _log.warn("Playlist could not be retrieved");
                }
            }
            else if (uri.isAlbumLink())
            {
                _log.debug("Received album, processing for tracks");
                // Have album
                Album album = _jahSpotify.readAlbum(uri);
                if (album != null)
                {
                    for (Link track : album.getTracks())
                    {
                        allURIs.add(track);
                    }
                    _log.debug("Album processed ...");
                }
                else
                {
                    _log.warn("Album could not be retrieved");
                }
            }
            else if (uri.isTrackLink())
            {
                // Have track
                allURIs.add(uri);
            }
        }

        if (_log.isDebugEnabled())
        {
            _log.debug("Queueing tracks: " + allURIs);
        }

        final CurrentQueue currentQueue = addTracksToQueue(allURIs);

        if (_log.isDebugEnabled())
        {
            _log.debug("Tracks queued: " + allURIs);
        }

        return currentQueue;
    }

    public CurrentQueue addToQueue(Link... uris)
    {
        return addToQueue(Arrays.asList(uris));
    }

    private CurrentQueue addTracksToQueue(final List<Link> trackURIs)
    {
        _queueLock.lock();
        try
        {
            final List<QueueTrack> queuedTracks = new ArrayList<QueueTrack>();
            // Add all elements to the array
            for (Link trackURI : trackURIs)
            {
                QueueTrack queuedTrack = new QueueTrack("jahspotify:queue:default:" + UUID.randomUUID().toString(), trackURI);
                queuedTracks.add(queuedTrack);
                _uriQueue.add(queuedTrack);
            }

            final QueueTrack[] queueTracks = new QueueTrack[queuedTracks.size()];
            queuedTracks.toArray(queueTracks);

            // Update the maximum queue size seen if the queue size is now the biggest ever seen
            if (_uriQueue.size() > _maxQueueSize)
            {
                _maxQueueSize = _uriQueue.size();
            }

            CurrentQueue currentQueue = getCurrentQueue(0);

            for (final QueueListener queueListener : _queueListeners)
            {
                queueListener.tracksAdded(queueTracks);
            }

            return currentQueue;
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public int deleteQueuedTrack(Link uri)
    {
        // uris are in the form of:
        // spotify:track:...
        // jahspotify:queue:<quename>:<uuid>

        final List<QueueTrack> removedTracks = new ArrayList<QueueTrack>();
        int count = 0;

        if (uri.isTrackLink())
        {
            for (QueueTrack queuedTrack : _uriQueue)
            {
                if (queuedTrack.getTrackUri().equals(uri))
                {
                    removedTracks.add(queuedTrack);
                    _uriQueue.remove(queuedTrack);
                    count++;
                }
            }
        }
        else if (uri.isQueueLink())
        {
           for (QueueTrack queuedTrack : _uriQueue)
            {
                // Extract the ID from the URI
                String queueId = uri.getQueueId();
                if (queuedTrack.getId().equals(queueId))
                {
                    removedTracks.add(queuedTrack);
                    _uriQueue.remove(queuedTrack);
                    count++;
                }
            }
        }

        if (count > 0)
        {
            for (QueueListener queueListener : _queueListeners)
            {
                queueListener.tracksRemoved(removedTracks.toArray(new QueueTrack[removedTracks.size()]));
            }
        }

        return count;
    }

    public QueueStatus getQueueStatus()
    {
        final QueueStatus queueStatus = new QueueStatus();
        queueStatus.setMediaPlayerState(_mediaPlayer.getMediaPlayerState());
        queueStatus.setTotalTracksPlayed(_numTracksPlayed);
        queueStatus.setTotalTracksCompleted(_numTracksPlayed - _numTracksSkipped);
        queueStatus.setTotalTracksSkipped(_numTracksSkipped);
        queueStatus.setTotalPlaytime(_totalPlayTime + (_mediaPlayer.getMediaPlayerState() == MediaPlayerState.PLAYING ? (System.currentTimeMillis() - _currentTrackStart) / 1000 : 0));
        queueStatus.setCurrentQueueSize(_uriQueue.size());
        queueStatus.setMaxQueueSize(_maxQueueSize);

        return queueStatus;
    }

    public QueueConfiguration getQueueConfiguration(Link uri)
    {
        if (!uri.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        return new QueueConfiguration(_repeatCurrentQueue, _repeatCurrentTrack, _shuffle);
    }

    public void setQueueConfiguration(Link uri, QueueConfiguration queueConfiguration)
    {
        if (!uri.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        _repeatCurrentQueue = queueConfiguration.isRepeatCurrentQueue();
        _repeatCurrentTrack = queueConfiguration.isRepeatCurrentTrack();
        _shuffle = queueConfiguration.isShuffle();
    }

    public CurrentQueue getCurrentQueue(final int count)
    {
        _queueLock.lock();
        try
        {
            final ArrayList<QueueTrack> queueTracks = new ArrayList<QueueTrack>(_uriQueue);
            // final int toIndex = count == 0 ? queueTracks.size() - 1 : count - 1;
            return new CurrentQueue(_currentTrack, queueTracks);
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public CurrentQueue shuffle(final Link uri)
    {
        _queueLock.lock();
        try
        {
            final List<QueueTrack> queuedTracks = new ArrayList<QueueTrack>(_uriQueue);
            _uriQueue.clear();
            Collections.shuffle(queuedTracks);
            _uriQueue.addAll(queuedTracks);
            return getCurrentQueue(0);
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public void addQueueListener(final QueueListener queueListener)
    {
        _queueListeners.add(queueListener);
    }

    public QueueTrack getNextQueueTrack()
    {
        final QueueTrack nextTrack = _uriQueue.poll();
        _log.debug("Retrieved next track: " + nextTrack);
        return nextTrack;

    }
}
