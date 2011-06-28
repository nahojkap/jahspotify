package jahspotify.metadata.search;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class TrackSearchResult extends SearchResult
{
    private List<TrackResult> tracks;

    public List<TrackResult> getTracks()
    {
        return tracks;
    }

    public void setTracks(final List<TrackResult> tracks)
    {
        this.tracks = tracks;
    }

    @Override
    public String toString()
    {
        return "TrackSearchResult{" +
                "tracks=" + tracks +
                "} " + super.toString();
    }
}
