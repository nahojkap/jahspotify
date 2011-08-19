package jahspotify;

import jahspotify.query.*;

/**
 * @author Johan Lindquist
 */
public abstract class Query
{
    public static Query and(Query left,Query right)
    {
        return AndQuery.and(left, right);
    }

    public Query and(Query right)
    {
        return AndQuery.and(this, right);
    }

    public static Query artist(String artist)
    {
        return new ArtistQuery(artist);
    }

     public static Query not(Query query)
    {
        return NotQuery.not(query);
    }

    public static Query or(Query left, Query right)
    {
        return OrQuery.or(left, right);
    }

    public Query or(Query right)
    {
        return OrQuery.or(this, right);
    }

    public abstract String serialize();

    public static TokenQuery token(final String token)
    {
        return TokenQuery.token(token);
    }

    public Query not()
    {
        return NotQuery.not(this);
    }
}
