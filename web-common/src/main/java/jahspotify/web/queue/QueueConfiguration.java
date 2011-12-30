package jahspotify.web.queue;

/**
 * @author Johan Lindquist
 */
public class QueueConfiguration
{
    private String callbackURL;
    private boolean reportTrackChanges;
    private boolean reportEmptyQueue;
    private boolean autoRefill;
    private String remoteQueueName;

    private boolean repeatCurrentTrack;
    private boolean repeatCurrentQueue;
    private boolean shuffle;

    public boolean isAutoRefill()
    {
        return autoRefill;
    }

    public void setAutoRefill(final boolean autoRefill)
    {
        this.autoRefill = autoRefill;
    }

    public String getCallbackURL()
    {
        return callbackURL;
    }

    public void setCallbackURL(final String callbackURL)
    {
        this.callbackURL = callbackURL;
    }

    public String getRemoteQueueName()
    {
        return remoteQueueName;
    }

    public void setRemoteQueueName(final String remoteQueueName)
    {
        this.remoteQueueName = remoteQueueName;
    }

    public boolean isReportEmptyQueue()
    {
        return reportEmptyQueue;
    }

    public void setReportEmptyQueue(final boolean reportEmptyQueue)
    {
        this.reportEmptyQueue = reportEmptyQueue;
    }

    public boolean isReportTrackChanges()
    {
        return reportTrackChanges;
    }

    public void setReportTrackChanges(final boolean reportTrackChanges)
    {
        this.reportTrackChanges = reportTrackChanges;
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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueConfiguration))
        {
            return false;
        }

        final QueueConfiguration that = (QueueConfiguration) o;

        if (autoRefill != that.autoRefill)
        {
            return false;
        }
        if (repeatCurrentQueue != that.repeatCurrentQueue)
        {
            return false;
        }
        if (repeatCurrentTrack != that.repeatCurrentTrack)
        {
            return false;
        }
        if (reportEmptyQueue != that.reportEmptyQueue)
        {
            return false;
        }
        if (reportTrackChanges != that.reportTrackChanges)
        {
            return false;
        }
        if (shuffle != that.shuffle)
        {
            return false;
        }
        if (callbackURL != null ? !callbackURL.equals(that.callbackURL) : that.callbackURL != null)
        {
            return false;
        }
        if (remoteQueueName != null ? !remoteQueueName.equals(that.remoteQueueName) : that.remoteQueueName != null)
        {
            return false;
        }

        return true;
    }

     public String toString()
    {
        return "QueueConfiguration{" +
                "autoRefill=" + autoRefill +
                ", callbackURL='" + callbackURL + '\'' +
                ", reportTrackChanges=" + reportTrackChanges +
                ", reportEmptyQueue=" + reportEmptyQueue +
                ", remoteQueueName='" + remoteQueueName + '\'' +
                ", repeatCurrentTrack=" + repeatCurrentTrack +
                ", repeatCurrentQueue=" + repeatCurrentQueue +
                ", shuffle=" + shuffle +
                '}';
    }

}
