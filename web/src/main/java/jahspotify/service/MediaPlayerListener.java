package jahspotify.service;

/**
 * @author Johan Lindquist
 */
public interface MediaPlayerListener
{
    public void trackStart(QueueTrack queueTrack);
    public void trackEnd(QueueTrack queueTrack, boolean forcedEnd);
    public void paused(QueueTrack currentTrack);
    public void resume(QueueTrack currentTrack);
    public void skip(QueueTrack currentTrack, QueueTrack nextTrack);
    public QueueTrack nextTrackToQueue();
}
