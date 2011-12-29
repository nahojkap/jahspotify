package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;
import javax.annotation.*;

import jahspotify.*;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.Link;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class MediaPlayer
{
    private MediaPlayerState _mediaPlayerState = MediaPlayerState.STOPPED;
    private Log _log = LogFactory.getLog(MediaPlayer.class);
    private BlockingQueue<Integer> _commandQueue = new ArrayBlockingQueue<Integer>(1, true);
    private QueueTrack _currentTrack;
    private Set<MediaPlayerListener> _mediaPlayerListeners = new HashSet<MediaPlayerListener>();

    public static final int QUEUE_NEXT = 0;
    public static final int QUEUE_QUIT = 1;

    @Autowired
    private QueueManager _queueManager;

    // Defines whether or not play should start immediately when a track is added to an empty queue
    @Value(value = "${jahspotify.player.auto-play-on-track-added}")
    private boolean _autoPlay = true;

    @Autowired
    private JahSpotifyService _jahSpotifyService;

    private JahSpotify _jahSpotify;

    @PreDestroy
    public void shutdown()
    {
        try
        {
            // Issue the shutdown token
            if (!_commandQueue.offer(QUEUE_QUIT, 1000, TimeUnit.MILLISECONDS))
            {
                // If not accepted, simply report so
                _log.debug("Command to quit not accepted");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initialize()
    {
        _queueManager.addQueueListener(new AbstractQueueListener()
        {
            @Override
            public void tracksAdded(final Link queue, final QueueTrack... queueTracks)
            {
                if (_autoPlay && _currentTrack == null)
                {
                    nextTrack();
                }
            }
        });
        _jahSpotify = _jahSpotifyService.getJahSpotify();

        _jahSpotify.addPlaybackListener(new PlaybackListener()
        {
            @Override
            public void trackStarted(final Link link)
            {
                _log.debug("Track started: " + link);
                _log.debug("Current track: " + _currentTrack);

                if (_currentTrack.getTrackUri().equals(link))
                {
                }
                _mediaPlayerState = MediaPlayerState.PLAYING;

                _log.debug("Notifying " + _mediaPlayerListeners.size() + " media player listeners");
                for (final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    mediaPlayerListener.trackStart(_currentTrack);
                }

            }

            @Override
            public void trackEnded(final Link link, boolean forcedEnd)
            {
                try
                {
                    _log.debug("End of track: " + link + (forcedEnd ? " (forced)" : ""));

                    for (final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                    {
                        mediaPlayerListener.trackEnd(_currentTrack, forcedEnd);
                    }

                    if (!forcedEnd)
                    {
                        if (_currentTrack == null)
                        {
                            // Hmm
                            _log.debug("Current track is already null!");
                            return;
                        }

                        _mediaPlayerState = MediaPlayerState.STOPPED;
                        _currentTrack = null;
                        _commandQueue.add(QUEUE_NEXT);
                    }

                }
                catch (Exception e)
                {
                    _log.error("Error handling track ended callback: " + e.getMessage(), e);
                }
            }

            @Override
            public Link nextTrackToPreload()
            {
                // Query all media play listeners - since there are many of them, they
                // all assign a weight to the next track to be played.  Highest weight
                // will win.  This allows the current queue to override any 'dynamic' and 'similar'
                // tracks from being played until the queue is empty (for example)

                _log.debug("Evaluating next track to pre-load");

                final List<QueueNextTrack> nextTracks = new ArrayList<QueueNextTrack>();
                for (MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    QueueNextTrack queueNextTrack = mediaPlayerListener.nextTrackToQueue();
                    if (queueNextTrack != null)
                    {
                        nextTracks.add(queueNextTrack);
                    }
                }

                if (!nextTracks.isEmpty())
                {
                    _log.info("Received " + nextTracks.size() + " track(s) for next track");

                    // Sort the tracks according to their weight
                    Collections.sort(nextTracks);

                    _log.info("Next tracks sorted according to weight: " + nextTracks);

                    // Take the next track in the collection no matter what - sorting would
                    // have prepared the list correctly in any case
                    QueueNextTrack queueTrack = nextTracks.get(0);

                    // We never repeat the current track - use repeat function instead
                    if (!queueTrack.equals(_currentTrack))
                    {
                        _log.info("Next track selected: " + queueTrack);
                        return queueTrack.getTrackUri();
                    }

                }
                _log.debug("No tracks to pre-load found");
                return null;
            }
        });

        final Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                boolean keepRunning = true;
                while (keepRunning)
                {
                    try
                    {
                        final Integer command = _commandQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if (command == null)
                        {
                            continue;
                        }
                        switch (command)
                        {
                            case QUEUE_NEXT:
                                final QueueTrack dequeuedTrack = _queueManager.getNextQueueTrack();

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
                                break;
                            case QUEUE_QUIT:
                                keepRunning = false;
                                break;
                            default:
                                _log.debug("Unrecognised command: " + command);
                        }
                    }
                    catch (Exception e)
                    {
                        _log.error("Error in queue monitor: " + e.getMessage(), e);
                    }
                }

            }
        });
        t.setDaemon(true);
        t.start();

    }

    public void pause()
    {
        switch (_mediaPlayerState)
        {
            case PLAYING:
                _jahSpotify.pause();
                _mediaPlayerState = MediaPlayerState.PAUSED;
                for (MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    mediaPlayerListener.paused(_currentTrack);
                }
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public void play()
    {
        switch (_mediaPlayerState)
        {
            case PAUSED:
                _jahSpotify.resume();
                _mediaPlayerState = MediaPlayerState.PLAYING;
                for (MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    mediaPlayerListener.resume(_currentTrack);
                }
                break;
            case STOPPED:
                // FIXME: Should this evaluate the queue?
                //nextTrack();
                break;
            default:
                // FIXME: Should really do something
        }
    }

    private void nextTrack()
    {
        try
        {
            _commandQueue.put(QUEUE_NEXT);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void skip()
    {
        // FIXME: Should also signal track skip with the mediaplayerlistener
        switch (_mediaPlayerState)
        {
            case PLAYING:
            case PAUSED:
                nextTrack();
                break;
            default:
                // FIXME: Should really do something
        }

    }

    public MediaPlayerState getMediaPlayerState()
    {
        return _mediaPlayerState;
    }

    public void addMediaPlayerListener(final MediaPlayerListener mediaPlayerListener)
    {
        _mediaPlayerListeners.add(mediaPlayerListener);
    }
}
