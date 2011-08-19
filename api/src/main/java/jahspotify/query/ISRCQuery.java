package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class ISRCQuery extends TokenQuery
{
    public ISRCQuery(final String isrc)
    {
        super("isrc:\"" + isrc +"\"");
    }

}
