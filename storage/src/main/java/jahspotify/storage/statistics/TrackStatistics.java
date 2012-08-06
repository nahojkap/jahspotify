package jahspotify.storage.statistics;

/**
 * @author Johan Lindquist
 */
public class TrackStatistics
{
    private long totalPlaytime;
    private int numTimesSkipped;
    private long lastPlayed;
    private long firstPlayed;
    private int numTimesCompleted;
    private int numTimesPlayed;

    public int getNumTimesPlayed()
    {
        return numTimesPlayed;
    }

    public void setNumTimesPlayed(final int numTimesPlayed)
    {
        this.numTimesPlayed = numTimesPlayed;
    }

    public long getFirstPlayed()
    {
        return firstPlayed;
    }

    public void setFirstPlayed(final long firstPlayed)
    {
        this.firstPlayed = firstPlayed;
    }

    public long getLastPlayed()
    {
        return lastPlayed;
    }

    public void setLastPlayed(final long lastPlayed)
    {
        this.lastPlayed = lastPlayed;
    }

    public int getNumTimesCompleted()
    {
        return numTimesCompleted;
    }

    public void setNumTimesCompleted(final int numTimesCompleted)
    {
        this.numTimesCompleted = numTimesCompleted;
    }

    public int getNumTimesSkipped()
    {
        return numTimesSkipped;
    }

    public void setNumTimesSkipped(final int numTimesSkipped)
    {
        this.numTimesSkipped = numTimesSkipped;
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
    public String toString()
    {
        return "TrackStatistics{" +
                "firstPlayed=" + firstPlayed +
                ", totalPlaytime=" + totalPlaytime +
                ", numTimesSkipped=" + numTimesSkipped +
                ", lastPlayed=" + lastPlayed +
                ", numTimesCompleted=" + numTimesCompleted +
                ", numTimesPlayed=" + numTimesPlayed +
                '}';
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

        if (firstPlayed != that.firstPlayed)
        {
            return false;
        }
        if (lastPlayed != that.lastPlayed)
        {
            return false;
        }
        if (numTimesCompleted != that.numTimesCompleted)
        {
            return false;
        }
        if (numTimesPlayed != that.numTimesPlayed)
        {
            return false;
        }
        if (numTimesSkipped != that.numTimesSkipped)
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
        result = 31 * result + (int) (lastPlayed ^ (lastPlayed >>> 32));
        result = 31 * result + (int) (firstPlayed ^ (firstPlayed >>> 32));
        result = 31 * result + numTimesCompleted;
        result = 31 * result + numTimesPlayed;
        return result;
    }
}
