package jahspotify.services;

import java.util.*;
import java.util.concurrent.*;
import javax.annotation.*;

import jahspotify.*;
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

    private Log _log = LogFactory.getLog( MediaPlayer.class );

    private BlockingQueue<Integer> _commandQueue = new ArrayBlockingQueue<Integer>( 1, true );

    private QueueTrack _currentTrack;
    private long _currentTrackLastStart;
    private int _currentTrackOffset;

    private Set<MediaPlayerListener> _mediaPlayerListeners = new HashSet<MediaPlayerListener>();

    public static final int SKIP = 0;

    public static final int QUIT = 1;

    public static final int STOP_PLAY = 2;

    @Autowired
    private QueueManager _queueManager;

    // Defines whether or not play should start immediately when a track is added to an empty queue
    @Value( value = "${jahspotify.player.auto-play-on-track-added}" )
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
            if ( ! _commandQueue.offer( QUIT, 1000, TimeUnit.MILLISECONDS ) )
            {
                // If not accepted, simply report so
                _log.debug( "Command to quit not accepted" );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initialize()
    {
        _queueManager.addQueueListener( new AbstractQueueListener()
        {
            @Override
            public void tracksAdded( final Link queue, final QueueTrack... queueTracks )
            {
                _log.debug( "Tracks added: " + Arrays.asList( queueTracks ) );
                _log.debug( "Auto-play: " + ( _autoPlay ? "yes" : "no" ) );
                _log.debug( "Current track: " + _currentTrack );
                if ( _autoPlay && _currentTrack == null )
                {
                    nextTrack();
                }
            }
        } );
        _jahSpotify = _jahSpotifyService.getJahSpotify();

        _jahSpotify.addPlaybackListener( new PlaybackListener()
        {
            @Override
            public void trackStarted( final Link link )
            {
                _log.debug( "Track started: " + link );
                _log.debug( "Current track: " + _currentTrack );

                _mediaPlayerState = MediaPlayerState.PLAYING;
                _currentTrackLastStart = System.currentTimeMillis();
                _currentTrackOffset = 0;

                _log.debug( "Notifying " + _mediaPlayerListeners.size() + " media player listeners" );
                for ( final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                        mediaPlayerListener.trackStart( _currentTrack );
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while informing listener: " + e.getMessage(), e );
                    }
                }

            }

            @Override
            public void trackEnded( final Link link, boolean forcedEnd )
            {
                try
                {
                    _log.debug( "End of track: " + link + ( forcedEnd ? " (forced)" : "" ) );

                    for ( final MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                    {
                        try
                        {
                            mediaPlayerListener.trackEnd( _currentTrack, forcedEnd );
                        }
                        catch ( Throwable e )
                        {
                            _log.error( "Error while informing listener: " + e.getMessage(), e );
                        }
                    }

                    if ( ! forcedEnd )
                    {
                        if ( _currentTrack == null )
                        {
                            // Hmm
                            _log.debug( "Current track is already null!" );
                            return;
                        }

                        _mediaPlayerState = MediaPlayerState.STOPPED;
                        _currentTrack = null;
                        _commandQueue.add( SKIP );
                    }

                }
                catch ( Exception e )
                {
                    _log.error( "Error handling track ended callback: " + e.getMessage(), e );
                }
            }

            @Override
            public Link nextTrackToPreload()
            {
                // Query all media play listeners - since there are many of them, they
                // all assign a weight to the next track to be played.  Highest weight
                // will win.  This allows the current queue to override any 'dynamic' and 'similar'
                // tracks from being played until the queue is empty (for example)

                _log.debug( "Evaluating next track to pre-load" );

                final List<QueueNextTrack> nextTracks = new ArrayList<QueueNextTrack>();
                for ( MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                    QueueNextTrack queueNextTrack = mediaPlayerListener.nextTrackToQueue();
                    if ( queueNextTrack != null )
                    {
                        nextTracks.add( queueNextTrack );
                    }
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while calling listener: " + e.getMessage(), e );
                    }
                }

                if ( ! nextTracks.isEmpty() )
                {
                    _log.info( "Received " + nextTracks.size() + " track(s) for next track" );

                    // Sort the tracks according to their weight
                    Collections.sort( nextTracks );

                    _log.info( "Next tracks sorted according to weight: " + nextTracks );

                    // Take the next track in the collection no matter what - sorting would
                    // have prepared the list correctly in any case
                    QueueNextTrack queueTrack = nextTracks.get( 0 );

                    // We never repeat the current track - use repeat function instead
                    if ( ! queueTrack.equals( _currentTrack ) )
                    {
                        _log.info( "Next track selected: " + queueTrack );
                        return queueTrack.getTrackUri();
                    }

                }
                _log.debug( "No tracks to pre-load found" );
                return null;
            }
        } );

        final Thread t = new Thread( new Runnable()
        {
            @Override
            public void run()
            {

                boolean keepRunning = true;
                while ( keepRunning )
                {
                    try
                    {
                        final Integer command = _commandQueue.poll( 200, TimeUnit.MILLISECONDS );
                        if ( command == null )
                        {
                            continue;
                        }
                        switch ( command )
                        {
                            case SKIP:
                                final QueueTrack dequeuedTrack = _queueManager.getNextQueueTrack();

                                if ( dequeuedTrack != null )
                                {
                                    _log.debug( "Initiating play of: " + dequeuedTrack );
                                    _currentTrack = dequeuedTrack;
                                    _jahSpotify.play( dequeuedTrack.getTrackUri() );
                                }
                                else
                                {
                                    // FIXME: This should handle the case where the last command was 'skip' and
                                    // the queue was empty.  Play should stop
                                    _log.debug( "Queue is empty, will stop play" );
                                    _jahSpotify.stop();
                                    _currentTrack = null;
                                }
                                break;
                            case QUIT:
                                keepRunning = false;
                                break;
                            case STOP_PLAY:
                                _jahSpotify.stop();
                                break;
                            default:
                                _log.debug( "Unrecognised command: " + command );
                        }
                    }
                    catch ( Exception e )
                    {
                        _log.error( "Error in queue monitor: " + e.getMessage(), e );
                    }
                }

            }
        } );
        t.setDaemon( true );
        t.start();

    }


    public void pause()
    {
        switch ( _mediaPlayerState )
        {
            case PLAYING:
                _jahSpotify.pause();
                _mediaPlayerState = MediaPlayerState.PAUSED;

                _currentTrackOffset = _currentTrackOffset + (int)(System.currentTimeMillis() - _currentTrackLastStart);

                for ( MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                        mediaPlayerListener.paused( _currentTrack );
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while informing listener: " + e.getMessage(), e );
                    }
                }
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public void seek( int offset )
    {
        switch ( _mediaPlayerState )
        {
            case PLAYING:
                _jahSpotify.seek( offset );

                // FIXME: Update the offset now
                _currentTrackOffset = offset;

                for ( MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                        mediaPlayerListener.seek( _currentTrack, offset );
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while informing listener: " + e.getMessage(), e );
                    }
                }
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public void stop()
    {
        switch ( _mediaPlayerState )
        {
            case PLAYING:
                _currentTrackOffset = _currentTrackOffset + (int)(System.currentTimeMillis() - _currentTrackLastStart);
            case PAUSED:
                _jahSpotify.stop();
                _mediaPlayerState = MediaPlayerState.STOPPED;

                for ( MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                        mediaPlayerListener.stopped( _currentTrack );
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while informing listener: " + e.getMessage(), e );
                    }
                }
                break;
            default:
                // FIXME: Should really do something
        }
    }

    public void play()
    {
        switch ( _mediaPlayerState )
        {
            case PAUSED:
                _jahSpotify.resume();
                _mediaPlayerState = MediaPlayerState.PLAYING;

                // FIXME: Update the offset now
                _currentTrackLastStart = System.currentTimeMillis();

                for ( MediaPlayerListener mediaPlayerListener : _mediaPlayerListeners )
                {
                    try
                    {
                        mediaPlayerListener.resume( _currentTrack );
                    }
                    catch ( Throwable e )
                    {
                        _log.error( "Error while informing listener: " + e.getMessage(), e );
                    }
                }
                break;
            case STOPPED:
                // If the current track is not null, we stopped play in the middle of the track
                if ( _currentTrack != null )
                {
                    _jahSpotify.play( _currentTrack.getTrackUri() );
                }
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
            _commandQueue.put( SKIP );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

    public void skip()
    {
        // FIXME: Should also signal track skip with the mediaplayerlistener
        switch ( _mediaPlayerState )
        {
            case PLAYING:
            case PAUSED:
            case STOPPED:
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

    public MediaPlayerStatus getMediaPlayerStatus()
    {
        final MediaPlayerStatus mediaPlayerStatus = new MediaPlayerStatus();

        int offset = 0;
        switch (_mediaPlayerState)
        {
            case PLAYING:
                offset = _currentTrackOffset + (int)(System.currentTimeMillis() - _currentTrackLastStart);
                break;
            case STOPPED:
            case PAUSED:
                offset = _currentTrackOffset;
        }

        mediaPlayerStatus.setCurrentTrack(_currentTrack);
        mediaPlayerStatus.setMediaPlayerState(_mediaPlayerState);
        mediaPlayerStatus.setTrackCurrentOffset(offset);
        mediaPlayerStatus.setTrackDuration(_currentTrack.getLength());

        return mediaPlayerStatus;
    }

    public void addMediaPlayerListener( final MediaPlayerListener mediaPlayerListener )
    {
        _mediaPlayerListeners.add( mediaPlayerListener );
    }

    public float getCurrentGain()
    {
        return _jahSpotify.getCurrentGain();
    }

    public void setCurrentGain( final float currentGain )
    {
        _jahSpotify.setCurrentGain( currentGain );
    }
}
