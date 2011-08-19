package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class LabelQuery extends TokenQuery
{
    public LabelQuery(final String label)
    {
        super("label:\"" + label +"\"");
    }

}
