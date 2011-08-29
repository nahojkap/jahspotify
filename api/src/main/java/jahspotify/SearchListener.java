package jahspotify;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public interface SearchListener
{
    public void searchComplete(SearchResult searchResult);

    public static class SearchResult
    {
        private List<Link> _tracksFound;
        private int _totalNumTracks;
        private int _trackOffset;
        private List<Link> _albumsFound;
        private int _totalNumAlbums;
        private int _albumOffset;
        private List<Link> _artistsFound;
        private int _totalNumArtists;
        private int _artistOffset;
    }
}
