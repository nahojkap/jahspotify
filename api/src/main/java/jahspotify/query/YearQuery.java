package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class YearQuery extends TokenQuery
{
    public YearQuery(final int year)
    {
        super("year:" + year);
    }

}
