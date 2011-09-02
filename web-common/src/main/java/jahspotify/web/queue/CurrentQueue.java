package jahspotify.web.queue;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private CurrentTrack currentlyPlaying;
    private List<QueuedTrack> queuedTracks;
    private QueueState queueState;

    private boolean shuffle;
    private boolean repeatCurrentQueue;
    private boolean repeatCurrentTrack;

    public QueueState getQueueState()
    {
        return queueState;
    }

    public void setQueueState(final QueueState queueState)
    {
        this.queueState = queueState;
    }

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
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof CurrentQueue))
        {
            return false;
        }

        final CurrentQueue that = (CurrentQueue) o;

        if (repeatCurrentQueue != that.repeatCurrentQueue)
        {
            return false;
        }
        if (repeatCurrentTrack != that.repeatCurrentTrack)
        {
            return false;
        }
        if (shuffle != that.shuffle)
        {
            return false;
        }
        if (currentlyPlaying != null ? !currentlyPlaying.equals(that.currentlyPlaying) : that.currentlyPlaying != null)
        {
            return false;
        }
        if (queueState != that.queueState)
        {
            return false;
        }
        if (queuedTracks != null ? !queuedTracks.equals(that.queuedTracks) : that.queuedTracks != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = currentlyPlaying != null ? currentlyPlaying.hashCode() : 0;
        result = 31 * result + (queuedTracks != null ? queuedTracks.hashCode() : 0);
        result = 31 * result + (queueState != null ? queueState.hashCode() : 0);
        result = 31 * result + (shuffle ? 1 : 0);
        result = 31 * result + (repeatCurrentQueue ? 1 : 0);
        result = 31 * result + (repeatCurrentTrack ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "CurrentQueue{" +
                "currentlyPlaying=" + currentlyPlaying +
                ", queuedTracks=" + queuedTracks +
                ", queueState=" + queueState +
                ", shuffle=" + shuffle +
                ", repeatCurrentQueue=" + repeatCurrentQueue +
                ", repeatCurrentTrack=" + repeatCurrentTrack +
                '}';
    }
}
