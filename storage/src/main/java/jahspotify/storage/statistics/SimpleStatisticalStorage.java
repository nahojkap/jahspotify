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
    public List<TrackHistory> getHistory(final long index, final long count, final HistoryCriteria... historyCriterias)
    {
        return _trackHistory.subList((int)index,(int)index+(int)count);
    }

    public void addTrackPlayed(final Link queue, final String source, final Link trackLink, final boolean completeTrackPlayed, final int secondsPlayed, final long startTime)
    {
        _log.debug("Adding track played: " + trackLink);
        _log.debug("Queue: " + queue);
        _log.debug("Source: " + source);
        _log.debug("Start time: " + startTime);
        _log.debug("Seconds played: " + secondsPlayed);
        _log.debug("Completed: " + (completeTrackPlayed ? "yes" : "no"));
        _trackHistory.add(0,new TrackHistory(queue, trackLink, source, completeTrackPlayed, secondsPlayed, startTime));

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
