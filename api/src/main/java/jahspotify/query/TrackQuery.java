package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class TrackQuery extends TokenQuery
{
    public TrackQuery(final String track)
    {
        super("track:\"" + track +"\"");
    }

}
