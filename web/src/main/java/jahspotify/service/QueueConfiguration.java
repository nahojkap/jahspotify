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
}
