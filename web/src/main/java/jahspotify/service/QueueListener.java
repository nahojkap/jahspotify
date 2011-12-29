package jahspotify.service;

import jahspotify.media.Link;

/** Defines listener callback methods for receiving events related to the current queue.
 * @author Johan Lindquist
 */
public interface QueueListener
{
    /**
     *
     * @param queue
     * @param queueTrack
     */
    public void newTrackAtFront(final Link queue, final QueueTrack queueTrack);

    /**
     *
     * @param queue The queue the tracks were added to
     * @param queueTracks
     */
    public void tracksAdded(final Link queue, final QueueTrack... queueTracks);

    /**
     *
     * @param queue The queue the tracks were removed from
     * @param queueTracks
     */
    public void tracksRemoved(final Link queue, final QueueTrack... queueTracks);

    /**
     *
     * @param queue
     * @param lastTrackPlayed
     */
    public void queueEmpty(final Link queue, final QueueTrack lastTrackPlayed);
}
