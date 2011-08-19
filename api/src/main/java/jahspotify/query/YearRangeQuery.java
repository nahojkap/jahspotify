package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class YearRangeQuery extends TokenQuery
{
    public YearRangeQuery(final int startYear, final int endYear)
    {
        super("year:" + startYear+"-" + endYear);
    }

}
