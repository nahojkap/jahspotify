package jahspotify.web;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private CurrentTrack currentlyPlaying;
    private List<QueuedTrack> queuedTracks;

    private boolean shuffle;
    private boolean repeatCurrentQueue;
    private boolean repeatCurrentTrack;

    public boolean isRepeatCurrentQueue()
    {
        return repeatCurrentQueue;
    }

    public void setRepeatCurrentQueue(final boolean repeatCurrentQueue)
    {
        this.repeatCurrentQueue = repeatCurrentQueue;
    }

    public boolean isRepeatCurrentTrack()
    {
        return repeatCurrentTrack;
    }

    public void setRepeatCurrentTrack(final boolean repeatCurrentTrack)
    {
        this.repeatCurrentTrack = repeatCurrentTrack;
    }

    public boolean isShuffle()
    {
        return shuffle;
    }

    public void setShuffle(final boolean shuffle)
    {
        this.shuffle = shuffle;
    }

    public CurrentTrack getCurrentlyPlaying()
    {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(final CurrentTrack currentlyPlaying)
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
                ", shuffle=" + shuffle +
                ", repeatCurrentQueue=" + repeatCurrentQueue +
                ", repeatCurrentTrack=" + repeatCurrentTrack +
                "} " + super.toString();
    }
}
