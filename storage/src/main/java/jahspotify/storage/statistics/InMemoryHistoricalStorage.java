package jahspotify.storage.statistics;

import java.util.*;

import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "in-memory")
public class InMemoryHistoricalStorage implements HistoricalStorage
{
    private Log _log = LogFactory.getLog(HistoricalStorage.class);

    private List<TrackHistory> _trackHistory = new ArrayList<TrackHistory>();

    @Override
    public Collection<TrackHistory> getHistory(final int index, final int count, final HistoryCriteria... historyCriterias)
    {
        return new AbstractCollection<TrackHistory>()
        {
            @Override
            public Iterator<TrackHistory> iterator()
            {
                return new InMemoryHistoryCursor(_trackHistory);
            }

            @Override
            public int size()
            {
                return _trackHistory.size();
            }
        };
    }

    public void addHistory(final TrackHistory trackHistory)
    {
        _log.debug("Adding track history: " + trackHistory);
        _trackHistory.add(0, trackHistory);

    }

    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink)
    {
        return null;
    }

    public TrackStatisticsCursor trackStatistics(final Link trackLink, int startFrom, int count)
    {
        return null;
    }

    @Override
    public int getHistoryCount(final HistoryCriteria... historyCriterias)
    {
        return 0;
    }
}
