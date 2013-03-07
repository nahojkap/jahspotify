package jahspotify;

import jahspotify.query.*;

/** Helper class for creating syntactically correct query statements for Spotify.  It mainly handles the creation of
 * AND, OR and NOT statements but also includes shortcuts to create the various query tags.
 * <br/>
 * It currently cover the following query tags:
 * <br/>
 * <ul>
 *     <li>album</li>
 *     <li>artist</li>
 *     <li>track</li>
 *     <li>year</li>
 *     <li>year range</li>
 *     <li>isrc</li>
 *     <li>genre</li>
 * </ul>
 * <br/>
 * See the <code>jahspotify.query</code> package the latest, update query classes available.
 * <br/>
 * <strong>Note:</strong> Most of the tags are currently covered.  For un-supported tags it is still possible to create
 * a query for the using the <code>TokenQuery</code>.  Simply place the tag code you wish to use into this and it will
 * be passed as is to Spotify. Any escaping of quotes and such must be directly handled by the client if using
 * <code>TokenQuery</code> directly.
 *
 * @author Johan Lindquist
 */
public abstract class Query
{
    /** Creates an 'AND' query based on the two queries.
     *
     * @param left The query to be put on the left hand side of the AND
     * @param right The query to be put on the right hand side of the AND
     * @return The AND query
     */
    public static Query and(Query left,Query right)
    {
        return AndQuery.and(left, right);
    }

    /** Creates an 'AND' query based on this <code>Query</code> instance and the specified query.  The parameter given
     * would be placed on the right hand side of the AND
     *
     * @param right The query to be put on the
     * @return The AND query
     */
    public Query and(Query right)
    {
        return AndQuery.and(this, right);
    }

    /** Creates a new artist query.
     *
     * @param artist The artist query to create
     * @return The artist query
     */
    public static Query artist(String artist)
    {
        return new ArtistQuery(artist);
    }

    public static Query track(String track)
    {
        return new TrackQuery(track);
    }


    /** Creates a new NOT query.  This will take the specified query and return the NOT'd version of the same.
     *
     * @param query The query to NOT
     * @return The NOT'd query
     */
    public static Query not(Query query)
    {
        return NotQuery.not(query);
    }

    /**
     *
     * @param left
     * @param right
     * @return
     */
    public static Query or(Query left, Query right)
    {
        return OrQuery.or(left, right);
    }

    /**
     *
     * @param right
     * @return
     */
    public Query or(Query right)
    {
        return OrQuery.or(this, right);
    }

    /**
     *
     * @return
     */
    public abstract String serialize();

    /** Creates a plain token query based on the specified string.  This can be used to represent any query expresion
     * possible in Spotify.  This allows un-supported queries to still be used.
     *
     * @param token The token string to base the query on
     * @return The token query
     */
    public static Query token(final String token)
    {
        return TokenQuery.token(token);
    }

    public static Query text(final String text)
    {
        return new TextQuery(text);
    }

    /** Creates a NOT query based on this <code>Query</code> instance.
     *
     * @return This query instance NOT'd
     */
    public Query not()
    {
        return NotQuery.not(this);
    }
}
