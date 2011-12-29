package jahspotify.service;

/**
 * @author Johan Lindquist
 */
public class QueueConfiguration
{
    private boolean _repeatCurrentTrack;
    private boolean _repeatCurrentQueue;
    private boolean _shuffle;

    public QueueConfiguration(final boolean repeatCurrentQueue, final boolean repeatCurrentTrack, final boolean shuffle)
    {
        _repeatCurrentQueue = repeatCurrentQueue;
        _repeatCurrentTrack = repeatCurrentTrack;
        _shuffle = shuffle;
    }

    public QueueConfiguration()
    {
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
        return result;
    }

    @Override
    public String toString()
    {
        return "QueueConfiguration{" +
                "_repeatCurrentQueue=" + _repeatCurrentQueue +
                ", _repeatCurrentTrack=" + _repeatCurrentTrack +
                ", _shuffle=" + _shuffle +
                '}';
    }
}
