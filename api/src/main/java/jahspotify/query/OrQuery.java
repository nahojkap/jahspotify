package jahspotify.query;

import jahspotify.Query;

/**
 * @author Johan Lindquist
 */
public class OrQuery extends Query
{
    private Query _left;
    private Query _right;

    private OrQuery(final Query left, final Query right)
    {
        _left = left;
        _right = right;
    }


    public static Query or(Query left, Query right)
    {
        return new OrQuery(left, right);
    }

    @Override
    public String serialize()
    {
        return "(" + _left.serialize() + " OR " + _right.serialize() + ")";
    }
}
