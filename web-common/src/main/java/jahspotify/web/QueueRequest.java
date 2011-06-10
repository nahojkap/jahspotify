package jahspotify.web;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class QueueRequest
{
    private boolean autoPlay = true;
    private List<String> uriQueue;

    public boolean isAutoPlay()
    {
        return autoPlay;
    }

    public void setAutoPlay(final boolean autoPlay)
    {
        this.autoPlay = autoPlay;
    }

    public List<String> getURIQueue()
    {
        return uriQueue;
    }

    public void setURIQueue(final List<String> uriQueue)
    {
        this.uriQueue = uriQueue;
    }

    @Override
    public String toString()
    {
        return "QueueRequest{" +
                "autoPlay=" + autoPlay +
                ", uriQueue=" + uriQueue +
                '}';
    }
}
