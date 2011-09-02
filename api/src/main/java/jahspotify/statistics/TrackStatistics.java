package jahspotify.statistics;

/**
 * @author Johan Lindquist
 */
public class TrackStatistics
{
    private long totalPlaytime;
    private boolean skipped;
    private long startTime;

    public boolean isSkipped()
    {
        return skipped;
    }

    public void setSkipped(final boolean skipped)
    {
        this.skipped = skipped;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(final long startTime)
    {
        this.startTime = startTime;
    }

    public long getTotalPlaytime()
    {
        return totalPlaytime;
    }

    public void setTotalPlaytime(final long totalPlaytime)
    {
        this.totalPlaytime = totalPlaytime;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof TrackStatistics))
        {
            return false;
        }

        final TrackStatistics that = (TrackStatistics) o;

        if (skipped != that.skipped)
        {
            return false;
        }
        if (startTime != that.startTime)
        {
            return false;
        }
        if (totalPlaytime != that.totalPlaytime)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (totalPlaytime ^ (totalPlaytime >>> 32));
        result = 31 * result + (skipped ? 1 : 0);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "TrackStatistics{" +
                "skipped=" + skipped +
                ", totalPlaytime=" + totalPlaytime +
                ", startTime=" + startTime +
                '}';
    }
}
