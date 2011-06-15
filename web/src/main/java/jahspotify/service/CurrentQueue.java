package jahspotify.service;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private Link _currentlyPlaying;
    private List<QueuedTrack> _queuedTracks;

    public CurrentQueue(final Link currentlyPlaying, final List<QueuedTrack> queuedTracks)
    {
        _currentlyPlaying = currentlyPlaying;
        _queuedTracks = queuedTracks;
    }

    public Link getCurrentlyPlaying()
    {
        return _currentlyPlaying;
    }

    public void setCurrentlyPlaying(final Link currentlyPlaying)
    {
        _currentlyPlaying = currentlyPlaying;
    }

    public List<QueuedTrack> getQueuedTracks()
    {
        return _queuedTracks;
    }

    public void setQueuedTracks(final List<QueuedTrack> queuedTracks)
    {
        _queuedTracks = queuedTracks;
    }
}
