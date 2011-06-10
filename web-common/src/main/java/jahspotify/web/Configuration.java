package jahspotify.web;

/**
 * @author Johan Lindquist
 */
public class Configuration
{
    private String callbackURL;
    private boolean reportTrackChanges;
    private boolean reportEmptyQueue;
    private boolean autoRefill;
    private String remoteQueueName;

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
}
