package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class TextQuery extends TokenQuery
{
    public TextQuery(final String text)
    {
        super("\""+ text +"\"");
    }
}
