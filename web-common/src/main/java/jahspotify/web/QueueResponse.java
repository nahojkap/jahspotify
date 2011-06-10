package jahspotify.web;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class QueueResponse extends BasicResponse
{
    private String currentlyPlaying;
    private List<QueuedTrack> queuedTracks;

    public String getCurrentlyPlaying()
    {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(final String currentlyPlaying)
    {
        this.currentlyPlaying = currentlyPlaying;
    }

    public List<QueuedTrack> getQueuedTracks()
    {
        return queuedTracks;
    }

    public void setQueuedTracks(final List<QueuedTrack> queuedTracks)
    {
        this.queuedTracks = queuedTracks;
    }

    @Override
    public String toString()
    {
        return "QueueResponse{" +
                "currentlyPlaying='" + currentlyPlaying + '\'' +
                ", queuedTracks=" + queuedTracks +
                '}';
    }
}
