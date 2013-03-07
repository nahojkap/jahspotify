package jahspotify.services;

import java.net.URL;

/**
 * @author Johan Lindquist
 */
public class QueueConfiguration
{
    private boolean _repeatCurrentTrack;
    private boolean _repeatCurrentQueue;
    private boolean _shuffle;
    private boolean _autoRefill = true;
    private URL _callbackURL;
    private boolean _reportTrackChanges;
    private boolean _reportEmptyQueue;

    public boolean isReportEmptyQueue()
    {
        return _reportEmptyQueue;
    }

    public void setReportEmptyQueue(final boolean reportEmptyQueue)
    {
        _reportEmptyQueue = reportEmptyQueue;
    }

    public boolean isReportTrackChanges()
    {
        return _reportTrackChanges;
    }

    public void setReportTrackChanges(final boolean reportTrackChanges)
    {
        _reportTrackChanges = reportTrackChanges;
    }

    public URL getCallbackURL()
    {
        return _callbackURL;
    }

    public void setCallbackURL(final URL callbackURL)
    {
        _callbackURL = callbackURL;
    }

    public boolean isAutoRefill()
    {
        return _autoRefill;
    }

    public void setAutoRefill(final boolean autoRefill)
    {
        _autoRefill = autoRefill;
    }

    public boolean isRepeatCurrentQueue()
    {
        return _repeatCurrentQueue;
    }

    public void setRepeatCurrentQueue(final boolean repeatCurrentQueue)
    {
        _repeatCurrentQueue = repeatCurrentQueue;
    }

    public boolean isRepeatCurrentTrack()
    {
        return _repeatCurrentTrack;
    }

    public void setRepeatCurrentTrack(final boolean repeatCurrentTrack)
    {
        _repeatCurrentTrack = repeatCurrentTrack;
    }

    public boolean isShuffle()
    {
        return _shuffle;
    }

    public void setShuffle(final boolean shuffle)
    {
        _shuffle = shuffle;
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

        if (_autoRefill != that._autoRefill)
        {
            return false;
        }
        if (_repeatCurrentQueue != that._repeatCurrentQueue)
        {
            return false;
        }
        if (_repeatCurrentTrack != that._repeatCurrentTrack)
        {
            return false;
        }
        if (_shuffle != that._shuffle)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (_repeatCurrentTrack ? 1 : 0);
        result = 31 * result + (_repeatCurrentQueue ? 1 : 0);
        result = 31 * result + (_shuffle ? 1 : 0);
        result = 31 * result + (_autoRefill ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueConfiguration{" +
                "_autoRefill=" + _autoRefill +
                ", _repeatCurrentTrack=" + _repeatCurrentTrack +
                ", _repeatCurrentQueue=" + _repeatCurrentQueue +
                ", _shuffle=" + _shuffle +
                '}';
    }
}
