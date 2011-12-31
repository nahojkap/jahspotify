package jahspotify.storage.statistics;

import java.util.*;

import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class SimpleStatisticalStorage implements HistoricalStorage
{
    private Log _log = LogFactory.getLog(HistoricalStorage.class);

    private List<TrackHistory> _trackHistory = new ArrayList<TrackHistory>();
    @Override
    public List<TrackHistory> getHistory(final int index, final int count, final HistoryCriteria... historyCriterias)
    {
        return _trackHistory;
    }

    public void addTrackPlayed(final Link trackLink, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime)
    {
        _log.debug("Adding track played: " + trackLink);
        _log.debug("Start time: " + startTime);
        _log.debug("Seconds played: " + secondsPlayed);
        _log.debug("Completed: " + (completeTrackPlayed ? "yes" : "no"));
        _trackHistory.add(0,new TrackHistory(trackLink, completeTrackPlayed, secondsPlayed, startTime));

    }
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink)
    {
        return null;
    }
    public List<TrackStatistics> trackStatistics(final Link trackLink, int startFrom, int count)
    {
        return null;
    }
}
