package jahspotify.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds biographical information about an artist.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class Biography
{
    /**
     * A Biographical text.
     */
    private String text;

    /**
     * A list of portrait image ids.
     */
    private List<String> portraits;

    /**
     * Creates an empty {@link Biography} object.
     */
    public Biography()
    {
        this(null);
    }

    /**
     * Creates a {@link Biography} object with the specified {@code text}.
     *
     * @param text A Biographical text.
     */
    public Biography(String text)
    {
        this.text = text;
        this.portraits = new ArrayList<String>();
    }

    /**
     * Get the biographical text.
     *
     * @return A Biographical text.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Set the biographical text.
     *
     * @param text A Biographical text.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Get a list of portrait image ids.
     *
     * @return A {@link List} of image ids.
     */
    public List<String> getPortraits()
    {
        return this.portraits;
    }

    /**
     * Set portraits for this biography.
     *
     * @param portraits A {@link List} of image ids.
     */
    public void setPortraits(List<String> portraits)
    {
        this.portraits = portraits;
    }
}
