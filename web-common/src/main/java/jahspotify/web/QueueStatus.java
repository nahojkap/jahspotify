package jahspotify.web;

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

}
