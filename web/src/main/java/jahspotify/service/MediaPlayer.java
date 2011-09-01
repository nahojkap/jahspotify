package jahspotify.service;

import java.util.*;
import java.util.concurrent.*;
import javax.annotation.PostConstruct;

import jahspotify.PlaybackListener;
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
    private JahSpotifyImpl _jahSpotify;
    private MediaPlayerState _mediaPlayerState;
    private Log _log = LogFactory.getLog(MediaPlayer.class);
    private BlockingQueue<String> _blockingQueue = new ArrayBlockingQueue<String>(1, true);
    private QueueTrack _currentTrack;
    private Set<MediaPlayerListener> _mediaPlayerListeners = new TreeSet<MediaPlayerListener>();

    @Autowired
    private QueueManager _queueManager;

    // Defines whether or not play should start immediately when a track is added to an empty queue
    @Value(value = "${jahspotify.player.auto-play-on-track-added}")
    private boolean _autoPlay = true;

    @PostConstruct
    public void initialize()
    {
        _queueManager.addQueueListener(new QueueListener()
        {
            @Override
            public void tracksAdded(final QueueTrack... queueTracks)
            {
                if (_autoPlay && _currentTrack == null)
                {
                    nextTrack();
                }
            }

            @Override
            public void tracksRemoved(final QueueTrack... queueTracks)
            {

            }
        });
        _jahSpotify = JahSpotifyImpl.getInstance();
        _jahSpotify.addPlaybackListener(new PlaybackListener()
        {
            @Override
            public void trackStarted(final String uriStr)
            {
                Link uri = Link.create(uriStr);

                _log.debug("Track started: " + uri);
                _log.debug("Current track: " + _currentTrack);

                _mediaPlayerState = MediaPlayerState.PLAYING;

                _log.debug("Notifying " + _mediaPlayerListeners.size() + " media player listeners");
                for (final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    mediaPlayerListener.trackStart(_currentTrack);
                }

            }

            @Override
            public void trackEnded(final String uri, boolean forcedEnd)
            {
                try
                {
                    final Link trackEnded = Link.create(uri);

                    _log.debug("End of track: " + trackEnded + (forcedEnd ? " (forced)" : ""));
                    if (!forcedEnd)
                    {
                        if (_currentTrack == null)
                        {
                            // Hmm
                            _log.debug("Current track is already null!");
                            return;
                        }

                        for (final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                        {
                            mediaPlayerListener.trackEnd(_currentTrack, forcedEnd);
                        }

                        _mediaPlayerState = MediaPlayerState.STOPPED;
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
                for (MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners)
                {
                    QueueTrack queueTrack = mediaPlayerListener.nextTrackToQueue();
                    if (queueTrack != null)
                    {
                        if (!queueTrack.equals(_currentTrack))
                        {
                            return queueTrack.getTrackUri().toString();
                        }
                    }
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
                        final String token = _blockingQueue.poll(1000, TimeUnit.MILLISECONDS);
                        if ("NEXT".equals(token))
                        {
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
            _blockingQueue.put("NEXT");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void skip()
    {
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
