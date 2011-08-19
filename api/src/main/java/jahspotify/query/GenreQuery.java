package jahspotify.query;

import jahspotify.Query;

/**
 * @author Johan Lindquist
 */
public class GenreQuery extends Query
{
    Genre _genre;

    public GenreQuery(final Genre genre)
    {
        _genre = genre;
    }

    @Override
    public String serialize()
    {
        return "genre:\"" + _genre.getTextual() + "\"";
    }
}
