package jahspotify.query;

import jahspotify.Query;

/**
 * @author Johan Lindquist
 */
public class AndQuery extends Query
{
    private Query _left;
    private Query _right;

    public AndQuery(final Query left, final Query right)
    {
        _left = left;
        _right = right;
    }

    public static Query and(Query left, Query right)
    {
        AndQuery andQuery = new AndQuery(left, right);
        return andQuery;
    }

    @Override
    public String serialize()
    {
        return "(" + _left.serialize() + " AND " + _right.serialize() + ")";
    }
}
