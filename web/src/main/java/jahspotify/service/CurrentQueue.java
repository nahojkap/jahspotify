package jahspotify.service;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private String _currentlyPlaying;
    private List<String> _queuedTracks;

    public CurrentQueue(final String currentlyPlaying, final List<String> queuedTracks)
    {
        _currentlyPlaying = currentlyPlaying;
        _queuedTracks = queuedTracks;
    }

    public String getCurrentlyPlaying()
    {
        return _currentlyPlaying;
    }

    public void setCurrentlyPlaying(final String currentlyPlaying)
    {
        _currentlyPlaying = currentlyPlaying;
    }

    public List<String> getQueuedTracks()
    {
        return _queuedTracks;
    }

    public void setQueuedTracks(final List<String> queuedTracks)
    {
        _queuedTracks = queuedTracks;
    }
}
