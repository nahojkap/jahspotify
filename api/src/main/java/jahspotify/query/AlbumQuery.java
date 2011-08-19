package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class AlbumQuery extends TokenQuery
{
    public AlbumQuery(final String album)
    {
        super("album:\"" + album +"\"");
    }

}
