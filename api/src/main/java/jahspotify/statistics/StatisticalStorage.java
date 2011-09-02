package jahspotify.statistics;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public interface StatisticalStorage
{
    public void addTrackPlayed(Link trackLink, boolean completeTrackPlayed, int secondsPlayed, long startTime);
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink);
    public List<TrackStatistics> trackStatistics(final Link trackLink, int startFrom, int count);
}
