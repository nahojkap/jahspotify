package jahspotify.statistics;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public interface StatisticalStorage
{
    public void addTrackPlayed(final Link trackLink, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime);
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink);
    public List<TrackStatistics> trackStatistics(final Link trackLink, int startFrom, int count);
}
