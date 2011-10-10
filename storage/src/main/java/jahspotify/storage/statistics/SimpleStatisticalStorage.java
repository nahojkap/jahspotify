package jahspotify.storage.statistics;

import java.util.List;

import jahspotify.media.Link;
import jahspotify.storage.statistics.*;

/**
 * @author Johan Lindquist
 */
public class SimpleStatisticalStorage implements StatisticalStorage
{
    @Override
    public void addTrackPlayed(final Link trackLink, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime)
    {

    }

    @Override
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink)
    {
        return null;
    }

    @Override
    public List<TrackStatistics> trackStatistics(final Link trackLink, final int startFrom, final int count)
    {
        return null;
    }
}
