package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import jahspotify.*;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.*;
import jahspotify.web.QueuedTrack;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class QueueManager
{
    private Log _log = LogFactory.getLog(QueueManager.class);

    private int _maxQueueSize;
    private int _numTracksPlayed;
    private int _numTracksSkipped;
    private int _totalPlayTime;

    private long _currentTrackStart;

    private QueueState _queueState = QueueState.STOPPED;
    private BlockingDeque<QueueTrack> _uriQueue = new LinkedBlockingDeque<QueueTrack>();
    private boolean _repeatCurrentQueue;
    private boolean _repeatCurrentTrack;
    private boolean _shuffle;
    private Lock _queueLock = new ReentrantLock();

    // Holder for our 'shuffle' information - this is reset whenever the status of the 'shuffle' flag is changed
    private Map<Link, Integer> _shuffleTracker = new HashMap<Link, Integer>();

    private BlockingQueue<String> _blockingQueue = new ArrayBlockingQueue<String>(1, true);
    private QueueTrack _currentTrack = null;
    private JahSpotifyImpl _jahSpotify;

    // Defines whether or not play should start immediately when a track is added to an empty queue
    @Value(value = "${jahspotify.queue.auto-play-on-track-added}")
    private boolean _autoPlay = true;
    private static final Link DEFAULT_QUEUE_LINK = Link.create("jahspotify:queue:default");

    private Set<QueueListener> _queueListeners = new TreeSet<QueueListener>();

    public QueueManager()
    {
        _jahSpotify = JahSpotifyImpl.getInstance();
        _jahSpotify.addPlaybackListener(new PlaybackListener()
        {
            @Override
            public void trackStarted(final String uri)
            {
                _log.debug("Track started: " + uri);
                _queueState = QueueState.PLAYING;
                _currentTrackStart = System.currentTimeMillis();
            }

            @Override
            public void trackEnded(final String uri, boolean forcedEnd)
            {
                try
                {
                    final Link trackEnded = Link.create(uri);

                    _queueState = QueueState.STOPPED;

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
                        _blockingQueue.add("NEXT");
                    }
                }
                catch (Exception e)
                {
                    _log.error("Error handling track ended callback: " + e.getMessage(), e);
                }
            }

            @Override
            public String nextTrackToPreload()
            {
                if (_repeatCurrentTrack)
                {
                    return null;
                }

                final QueueTrack peek = _uriQueue.peek();
                if (peek != null)
                {
                    return peek.getTrackUri().asString();
                }
                return null;
            }
        });


        final Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        String token = _blockingQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if ("NEXT".equals(token))
                        {
                            final QueueTrack dequeuedTrack = _uriQueue.poll();

                            if (dequeuedTrack != null)
                            {
                                _log.debug("Initiating play of: " + dequeuedTrack);
                                _currentTrack = dequeuedTrack;
                                _jahSpotify.play(dequeuedTrack.getTrackUri());
                            }
                            else
                            {
                                // FIXME: This should handle the case where the last command was 'skip' and
                                // the queue was empty.  Play should stop
                                _log.debug("Queue is empty, will have wait ...");
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    _log.error("Error in queue monitor: " + e.getMessage(), e);
                }
            }
        });
        t.start();
    }

    public CurrentQueue addToQueue(List<Link> uris)
    {
        // This should check if uris are:
        // - track (spotify:track:2zvot9pY2FNl1E94kc4K8M)
        // - album (spotify:album:46g6b33tbttcPtzbwzBoG6)
        // - playlist (spotify:user:<username>:playlist:5kAbHzlaZP8vMb6HVIWmdF)

        final List<Link> allURIs = new ArrayList<Link>();

        for (Link uri : uris)
        {
            if (uri.isPlaylistLink())
            {
                _log.debug("Received playlist, processing for tracks");
                // Have playlist
                Playlist playlist = _jahSpotify.readPlaylist(uri);
                for (Link track : playlist.getTracks())
                {
                    allURIs.add(track);
                }
                _log.debug("Playlist processed ...");
            }
            else if (uri.isAlbumLink())
            {
                _log.debug("Received album, processing for tracks");
                // Have album
                Album album = _jahSpotify.readAlbum(uri);
                for (Link track : album.getTracks())
                {
                    allURIs.add(track);
                }
                _log.debug("Album processed ...");
            }
            else if (uri.isTrackLink())
            {
                // Have track
                allURIs.add(uri);
            }
        }

        addTracksToQueue(allURIs);
        return getCurrentQueue();
    }

    public CurrentQueue addToQueue(Link... uris)
    {
        addToQueue(Arrays.asList(uris));
        return getCurrentQueue();
    }

    private void addTracksToQueue(final List<Link> trackURIs)
    {
        _queueLock.lock();
        try
        {
            // Add all elements to the array
            for (Link trackURI : trackURIs)
            {
                QueueTrack queuedTrack = new QueueTrack("jahspotify:queue:default:" + UUID.randomUUID().toString(), trackURI);
                _uriQueue.add(queuedTrack);
            }

            // Update the maximum queue size seen if the queue size is now the biggest ever seen
            if (_uriQueue.size() > _maxQueueSize)
            {
                _maxQueueSize = _uriQueue.size();
            }

            //
            if (_autoPlay && _currentTrack == null)
            {
                _blockingQueue.add("NEXT");
            }
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public void pause()
    {
        switch (_queueState)
        {
            case PLAYING:
                _jahSpotify.pause();
                _queueState = QueueState.PAUSED;
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public void play()
    {
        switch (_queueState)
        {
            case PAUSED:
                _jahSpotify.resume();
                _queueState = QueueState.PLAYING;
                break;
            case STOPPED:
                // FIXME: Should this evaluate the queue?
                nextTrack();
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public int deleteQueuedTrack(Link uri)
    {
        // uris are in the form of:
        // spotify:track:...
        // jahspotify:queue:<quename>:<uuid>

        int count = 0;

        if (uri.isTrackLink())
        {
            for (QueueTrack queuedTrack : _uriQueue)
            {
                if (queuedTrack.getTrackUri().equals(uri))
                {
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
                    _uriQueue.remove(queuedTrack);
                    count++;
                }
            }
        }

        return count;
    }

    private void nextTrack()
    {
        try
        {
            _blockingQueue.put("NEXT");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void skip()
    {
        switch (_queueState)
        {
            case PLAYING:
            case PAUSED:
                nextTrack();
                break;
            default:
                // FIXME: Should really do something
        }

    }

    public QueueStatus getQueueStatus()
    {
        final QueueStatus queueStatus = new QueueStatus();
        queueStatus.setQueueState(_queueState);
        queueStatus.setTotalTracksPlayed(_numTracksPlayed);
        queueStatus.setTotalTracksCompleted(_numTracksPlayed - _numTracksSkipped);
        queueStatus.setTotalTracksSkipped(_numTracksSkipped);
        queueStatus.setTotalPlaytime(_totalPlayTime + (_queueState == QueueState.PLAYING ? (System.currentTimeMillis() - _currentTrackStart) / 1000 : 0));
        queueStatus.setCurrentQueueSize(_uriQueue.size());
        queueStatus.setMaxQueueSize(_maxQueueSize);

        return queueStatus;
    }

    public QueueState getQueueState()
    {
        return _queueState;
    }

    public QueueConfiguration getQueueConfiguration(Link uri)
    {
        if (!uri.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        final QueueConfiguration queueConfiguration = new QueueConfiguration(_repeatCurrentQueue, _repeatCurrentTrack, _shuffle);
        return queueConfiguration;
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

    public CurrentQueue getCurrentQueue()
    {
        _queueLock.lock();
        try
        {
            return new CurrentQueue(_currentTrack, new ArrayList<QueueTrack>(_uriQueue));
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
            return getCurrentQueue();
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
}
