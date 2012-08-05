package jahspotify.storage.statistics;

import java.util.Collection;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface HistoricalStorage
{
    public Collection<TrackHistory> getHistory(int index, int count, HistoryCriteria... historyCriterias);
    public void addHistory(final TrackHistory trackHistory);
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink);
    public TrackStatisticsCursor trackStatistics(final Link trackLink, int startFrom, int count);
    public int getHistoryCount(HistoryCriteria... historyCriterias);
}
