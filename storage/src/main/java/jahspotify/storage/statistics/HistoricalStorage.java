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
    public TrackStatistics getTrackStatistics(final Link trackLink);
    public int getHistoryCount(HistoryCriteria... historyCriterias);
}
