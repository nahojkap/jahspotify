package jahspotify.query;

import jahspotify.Query;

/**
 * @author Johan Lindquist
 */
public class NotQuery extends Query
{
    private Query _query;

    private NotQuery(final Query query)
    {
        _query = query;
    }

    public static Query not(Query query)
    {
        return new NotQuery(query);
    }

    @Override
    public String serialize()
    {
        return "( NOT (" + _query.serialize() +"))";
    }
}
