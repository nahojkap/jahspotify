package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;

import jahspotify.*;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.*;
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
    private Queue<QueuedTrack> _uriQueue = new ConcurrentLinkedQueue<QueuedTrack>();

    private BlockingQueue<String> _blockingQueue = new ArrayBlockingQueue<String>(1, true);
    private String _currentURI = null;
    private JahSpotifyImpl _jahSpotify;

    // Defines whether or not play should start immediately when a track is added to an empty queue
    @Value(value = "${jahspotify.queue.auto-play-on-track-added}")
    private boolean _autoPlay = true;

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
                // throw new RuntimeException("Remove this once the callback is correctly in place - also remember to increment the counter below");
            }

            @Override
            public void trackEnded(final String uri, boolean forcedEnd)
            {
                _queueState = QueueState.STOPPED;

                _numTracksPlayed++;
                _totalPlayTime += ((System.currentTimeMillis() - _currentTrackStart) / 1000);
                if (forcedEnd)
                {
                    _numTracksSkipped++;
                }

                _log.debug("End of track: " + uri + (forcedEnd ? " (forced)" : ""));
                if (!forcedEnd)
                {
                    if (_currentURI == null)
                    {
                        // Hmm
                        _log.debug("Current URI is already null!");
                        return;
                    }
                    if (!uri.equals(_currentURI))
                    {
                        _log.debug("Current URI don't match what is ending: " + uri + " != " + _currentURI);
                        return;
                    }
                    _currentURI = null;
                    _blockingQueue.add("NEXT");
                }
            }

            @Override
            public String nextTrackToPreload()
            {
                final QueuedTrack peek = _uriQueue.peek();
                if (peek != null)
                {
                    return peek.getTrackID();
                }
                return null;
            }
        });


        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        String token = _blockingQueue.poll(100, TimeUnit.SECONDS);
                        if ("NEXT".equals(token))
                        {
                            final QueuedTrack queuedTrack = _uriQueue.poll();

                            if (queuedTrack != null)
                            {
                                _log.debug("Initiating play of: " + queuedTrack.getTrackID());
                                _currentURI = queuedTrack.getTrackID();
                                _jahSpotify.play(queuedTrack.getTrackID());
                            }
                            else
                            {
                                _log.debug("Queue is empty, will have wait ...");
                            }
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addToQueue(List<String> uris)
    {
        // This should check if uris are:
        // - track (spotify:track:2zvot9pY2FNl1E94kc4K8M)
        // - album (spotify:album:46g6b33tbttcPtzbwzBoG6)
        // - playlist (spotify:user:<username>:playlist:5kAbHzlaZP8vMb6HVIWmdF)

        final List<String> allURIs = new ArrayList<String>();

        for (String s : uris)
        {
            if (s.matches("spotify:user:.*:playlist:.*"))
            {
                _log.debug("Received playlist, processing for tracks");
                // Have playlist
                Playlist playlist = _jahSpotify.readPlaylist(s);
                for (Track track : playlist.getTracks())
                {
                    allURIs.add(track.getId());
                }
                _log.debug("Playlist processed ...");
            }
            if (s.matches("spotify:album:.*"))
            {
                _log.debug("Received album, processing for tracks");
                // Have album
                Album album = _jahSpotify.readAlbum(s);
                for (Track track : album.getTracks())
                {
                    allURIs.add(track.getId());
                }
                _log.debug("Album processed ...");
            }
            if (s.matches("spotify:track:.*"))
            {
                // Have track
                allURIs.add(s);
            }
        }

        addTracksToQueue(allURIs);
    }

    public void addToQueue(String... uris)
    {
        addToQueue(Arrays.asList(uris));
    }

    private void addTracksToQueue(final List<String> trackURIs)
    {
        // Add all elements to the array
        for (String trackURI : trackURIs)
        {
            QueuedTrack queuedTrack = new QueuedTrack("jahspotify:queue:default:" + UUID.randomUUID().toString(), trackURI);
            _uriQueue.add(queuedTrack);
        }

        // Update the maximum queue size seen if the queue size is now the biggest ever seen
        if (_uriQueue.size() > _maxQueueSize)
        {
            _maxQueueSize = _uriQueue.size();
        }

        //
        if (_autoPlay && _currentURI == null)
        {
            _blockingQueue.add("NEXT");
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

    public int deleteQueuedTrack(String uri)
    {
        // uris are in the form of:
        // spotify:track:...
        // jahspotify:queue:<quename>:<uuid>

        int count = 0;

        if (uri.matches("spotify:track:.*"))
        {
            for (QueuedTrack queuedTrack : _uriQueue)
            {
                if (queuedTrack.getTrackID().equals(uri))
                {
                    _uriQueue.remove(queuedTrack);
                    count++;
                }
            }
        }
        if (uri.matches("jahspotify:queue:default:.*"))
        {
            for (QueuedTrack queuedTrack : _uriQueue)
            {
                if (queuedTrack.getId().equals(uri))
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

    public CurrentQueue getCurrentQueue()
    {
        return new CurrentQueue(_currentURI, new ArrayList<QueuedTrack>(_uriQueue));
    }
}
