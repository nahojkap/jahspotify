package jahspotify.query;

import jahspotify.Query;

/**
 * @author Johan Lindquist
 */
public class TokenQuery extends Query
{
    private String _token;

    TokenQuery(final String token)
    {
        _token = token;
    }

    @Override
    public String serialize()
    {
        return _token;
    }

    public static TokenQuery token(final String token)
    {
        return new TokenQuery(token);
    }
}
