package jahspotify.service;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface QueueListener
{
    public void tracksAdded(QueueTrack... queueTracks);
    public void trackStart(QueueTrack queueTrack);
    public void trackEnd(QueueTrack queueTrack);
    public void trackRemoved(QueueTrack queueTrack);
}
