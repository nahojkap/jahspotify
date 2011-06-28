package jahspotify.metadata.lookup;

/**
 * @author Johan Lindquist
 */
public class LookupResult
{
    LookupInfo info;

    ArtistMediaLookup artist;
    AlbumMediaLookup album;
    TrackMediaLookup track;

    public AlbumMediaLookup getAlbum()
    {
        return album;
    }

    public void setAlbum(final AlbumMediaLookup album)
    {
        this.album = album;
    }

    public ArtistMediaLookup getArtist()
    {
        return artist;
    }

    public void setArtist(final ArtistMediaLookup artist)
    {
        this.artist = artist;
    }

    public LookupInfo getInfo()
    {
        return info;
    }

    public void setInfo(final LookupInfo info)
    {
        this.info = info;
    }

    public TrackMediaLookup getTrack()
    {
        return track;
    }

    public void setTrack(final TrackMediaLookup track)
    {
        this.track = track;
    }

    @Override
    public String toString()
    {
        return "LookupResult{" +
                "album=" + album +
                ", info=" + info +
                ", artist=" + artist +
                ", track=" + track +
                '}';
    }
}
