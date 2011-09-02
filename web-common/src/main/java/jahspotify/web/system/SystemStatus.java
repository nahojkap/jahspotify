package jahspotify.web.system;

import jahspotify.web.queue.QueueStatus;

/**
 * @author Johan Lindquist
 */
public class SystemStatus
{
    private long upSince;

    private long freeMemory;
    private long totalMemory;
    private long maxMemory;

    private String callbackURL;
    private boolean reportTrackChanges;
    private boolean reportEmptyQueue;
    private boolean autoRefill;
    private String remoteQueueName;
    private int numberProcessors;

    private QueueStatus queueStatus;

    public long getMaxMemory()
    {
        return maxMemory;
    }

    public void setMaxMemory(final long maxMemory)
    {
        this.maxMemory = maxMemory;
    }

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

    public long getFreeMemory()
    {
        return freeMemory;
    }

    public void setFreeMemory(final long freeMemory)
    {
        this.freeMemory = freeMemory;
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

    public long getTotalMemory()
    {
        return totalMemory;
    }

    public void setTotalMemory(final long totalMemory)
    {
        this.totalMemory = totalMemory;
    }

    public long getUpSince()
    {
        return upSince;
    }

    public void setUpSince(final long upSince)
    {
        this.upSince = upSince;
    }

    public void setNumberProcessors(final int numberProcessors)
    {
        this.numberProcessors = numberProcessors;
    }

    public int getNumberProcessors()
    {
        return numberProcessors;
    }

    public void setQueueStatus(final QueueStatus queueStatus)
    {
        this.queueStatus = queueStatus;
    }

    public QueueStatus getQueueStatus()
    {
        return queueStatus;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SystemStatus))
        {
            return false;
        }

        final SystemStatus that = (SystemStatus) o;

        if (autoRefill != that.autoRefill)
        {
            return false;
        }
        if (freeMemory != that.freeMemory)
        {
            return false;
        }
        if (maxMemory != that.maxMemory)
        {
            return false;
        }
        if (numberProcessors != that.numberProcessors)
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
        if (totalMemory != that.totalMemory)
        {
            return false;
        }
        if (upSince != that.upSince)
        {
            return false;
        }
        if (callbackURL != null ? !callbackURL.equals(that.callbackURL) : that.callbackURL != null)
        {
            return false;
        }
        if (queueStatus != null ? !queueStatus.equals(that.queueStatus) : that.queueStatus != null)
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
        return "SystemStatus{" +
                "autoRefill=" + autoRefill +
                ", upSince=" + upSince +
                ", freeMemory=" + freeMemory +
                ", totalMemory=" + totalMemory +
                ", maxMemory=" + maxMemory +
                ", callbackURL='" + callbackURL + '\'' +
                ", reportTrackChanges=" + reportTrackChanges +
                ", reportEmptyQueue=" + reportEmptyQueue +
                ", remoteQueueName='" + remoteQueueName + '\'' +
                ", numberProcessors=" + numberProcessors +
                ", queueStatus=" + queueStatus +
                '}';
    }


}
