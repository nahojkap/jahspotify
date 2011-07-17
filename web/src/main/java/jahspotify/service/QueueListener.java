package jahspotify.service;

/**
 * @author Johan Lindquist
 */
public interface QueueListener
{
    public void tracksAdded(QueueTrack... queueTracks);
    public void tracksRemoved(QueueTrack... queueTracks);
}
