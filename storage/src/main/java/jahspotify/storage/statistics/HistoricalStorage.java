package jahspotify.storage.statistics;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface HistoricalStorage
{
    public HistoryCursor getHistory(int index, int count, HistoryCriteria... historyCriterias);
    public void addHistory(final TrackHistory trackHistory);
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink);
    public TrackStatisticsCursor trackStatistics(final Link trackLink, int startFrom, int count);
    public int getHistoryCount(HistoryCriteria... historyCriterias);
}
