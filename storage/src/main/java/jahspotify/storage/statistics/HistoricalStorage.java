package jahspotify.storage.statistics;

import java.util.List;

import jahspotify.media.*;
import jahspotify.storage.statistics.*;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public interface HistoricalStorage
{
    public List<TrackHistory> getHistory(int index, int count, HistoryCriteria... historyCriterias);
    public void addHistory(final TrackHistory trackHistory);
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink);
    public List<TrackStatistics> trackStatistics(final Link trackLink, int startFrom, int count);
}
