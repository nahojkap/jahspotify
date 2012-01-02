package jahspotify.web.queue;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class CurrentQueue
{
    private String id;

    private CurrentTrack currentlyPlaying;
    private List<QueuedTrack> queuedTracks;
    private QueueStatus queueStatus;
    private QueueConfiguration queueConfiguration;

    public QueueStatus getQueueStatus()
    {
        return queueStatus;
    }

    public void setQueueStatus(final QueueStatus queueStatus)
    {
        this.queueStatus = queueStatus;
    }

    public QueueConfiguration getQueueConfiguration()
    {
        return queueConfiguration;
    }

    public void setQueueConfiguration(final QueueConfiguration queueConfiguration)
    {
        this.queueConfiguration = queueConfiguration;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
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

        if (currentlyPlaying != null ? !currentlyPlaying.equals(that.currentlyPlaying) : that.currentlyPlaying != null)
        {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null)
        {
            return false;
        }
        if (queueConfiguration != null ? !queueConfiguration.equals(that.queueConfiguration) : that.queueConfiguration != null)
        {
            return false;
        }
        if (queueStatus != null ? !queueStatus.equals(that.queueStatus) : that.queueStatus != null)
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
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (currentlyPlaying != null ? currentlyPlaying.hashCode() : 0);
        result = 31 * result + (queuedTracks != null ? queuedTracks.hashCode() : 0);
        result = 31 * result + (queueStatus != null ? queueStatus.hashCode() : 0);
        result = 31 * result + (queueConfiguration != null ? queueConfiguration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "CurrentQueue{" +
                "currentlyPlaying=" + currentlyPlaying +
                ", id='" + id + '\'' +
                ", queuedTracks=" + queuedTracks +
                ", queueStatus=" + queueStatus +
                ", queueConfiguration=" + queueConfiguration +
                '}';
    }
}
