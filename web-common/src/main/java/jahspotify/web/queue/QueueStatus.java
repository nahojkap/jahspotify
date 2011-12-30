package jahspotify.web.queue;

import jahspotify.web.queue.QueueState;

/**
 * @author Johan Lindquist
 */
public class QueueStatus
{
    private int totalTracksPlayed;
    private int totalTracksCompleted;
    private int totalTracksSkipped;
    private long totalPlaytime;
    private int maxQueueSize;
    private int currentQueueSize;
    private QueueState queueState;

    public int getCurrentQueueSize()
    {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(final int currentQueueSize)
    {
        this.currentQueueSize = currentQueueSize;
    }

    public int getMaxQueueSize()
    {
        return maxQueueSize;
    }

    public void setMaxQueueSize(final int maxQueueSize)
    {
        this.maxQueueSize = maxQueueSize;
    }

    public long getTotalPlaytime()
    {
        return totalPlaytime;
    }

    public void setTotalPlaytime(final long totalPlaytime)
    {
        this.totalPlaytime = totalPlaytime;
    }

    public int getTotalTracksCompleted()
    {
        return totalTracksCompleted;
    }

    public void setTotalTracksCompleted(final int totalTracksCompleted)
    {
        this.totalTracksCompleted = totalTracksCompleted;
    }

    public int getTotalTracksPlayed()
    {
        return totalTracksPlayed;
    }

    public void setTotalTracksPlayed(final int totalTracksPlayed)
    {
        this.totalTracksPlayed = totalTracksPlayed;
    }

    public int getTotalTracksSkipped()
    {
        return totalTracksSkipped;
    }

    public void setTotalTracksSkipped(final int totalTracksSkipped)
    {
        this.totalTracksSkipped = totalTracksSkipped;
    }

    public void setQueueState(final QueueState queueState)
    {
        this.queueState = queueState;
    }

    public QueueState getQueueState()
    {
        return queueState;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueStatus))
        {
            return false;
        }

        final QueueStatus that = (QueueStatus) o;

        if (currentQueueSize != that.currentQueueSize)
        {
            return false;
        }
        if (maxQueueSize != that.maxQueueSize)
        {
            return false;
        }
        if (totalPlaytime != that.totalPlaytime)
        {
            return false;
        }
        if (totalTracksCompleted != that.totalTracksCompleted)
        {
            return false;
        }
        if (totalTracksPlayed != that.totalTracksPlayed)
        {
            return false;
        }
        if (totalTracksSkipped != that.totalTracksSkipped)
        {
            return false;
        }
        if (queueState != that.queueState)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = totalTracksPlayed;
        result = 31 * result + totalTracksCompleted;
        result = 31 * result + totalTracksSkipped;
        result = 31 * result + (int) (totalPlaytime ^ (totalPlaytime >>> 32));
        result = 31 * result + maxQueueSize;
        result = 31 * result + currentQueueSize;
        result = 31 * result + (queueState != null ? queueState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueStatus{" +
                "currentQueueSize=" + currentQueueSize +
                ", totalTracksPlayed=" + totalTracksPlayed +
                ", totalTracksCompleted=" + totalTracksCompleted +
                ", totalTracksSkipped=" + totalTracksSkipped +
                ", totalPlaytime=" + totalPlaytime +
                ", maxQueueSize=" + maxQueueSize +
                ", queueState=" + queueState +
                '}';
    }
}
