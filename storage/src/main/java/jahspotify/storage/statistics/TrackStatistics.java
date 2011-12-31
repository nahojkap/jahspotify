package jahspotify.storage.statistics;

/**
 * @author Johan Lindquist
 */
public class TrackStatistics
{
    private long totalPlaytime;
    private int numTimesSkipped;
    private long startTime;
    private int numTimesCompleted;


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

        if (numTimesCompleted != that.numTimesCompleted)
        {
            return false;
        }
        if (numTimesSkipped != that.numTimesSkipped)
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
        result = 31 * result + numTimesSkipped;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + numTimesCompleted;
        return result;
    }

    public int getNumTimesSkipped()
    {
        return numTimesSkipped;
    }

    public void setNumTimesSkipped(final int numTimesSkipped)
    {
        this.numTimesSkipped = numTimesSkipped;
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

    public int getNumTimesCompleted()
    {
        return numTimesCompleted;
    }

    public void setNumTimesCompleted(final int numTimesCompleted)
    {
        this.numTimesCompleted = numTimesCompleted;
    }

    @Override
    public String toString()
    {
        return "TrackStatistics{" +
                "numTimesCompleted=" + numTimesCompleted +
                ", totalPlaytime=" + totalPlaytime +
                ", numTimesSkipped=" + numTimesSkipped +
                ", startTime=" + startTime +
                '}';
    }
}
