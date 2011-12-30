package jahspotify.services.spotiseek;

import java.util.List;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class SpotiseekResult
{
    private List<SpotiseekResultItem> _spotiseekResultItems;

    public static class SpotiseekResultItem
    {
        String _name;
        Link _track;
        float _length;
        float _popularity;
        int _trackNumber;

        Link _album;
        String _albumName;
        int _releaseYear;

        List<Link> _artists;
        List<String> _artistNames;

        List<String> _availability;

    }
}
