package jahspotify.web;

/**
 * @author Johan Lindquist
 */
public class SystemStatusResponse extends BasicResponse
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

    private QueueStatusResponse queueStatusResponse;

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

    public void setQueueStatusResponse(final QueueStatusResponse queueStatusResponse)
    {
        this.queueStatusResponse = queueStatusResponse;
    }

    public QueueStatusResponse getQueueStatusResponse()
    {
        return queueStatusResponse;
    }
}
