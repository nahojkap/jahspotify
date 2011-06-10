package jahspotify.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jahspotify.util.Hex;

/**
 * Holds basic information about media.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class Media
{
    /**
     * Identifier for this media object (32-character hex string).
     */
    protected String id;

    /**
     * Redirects (other identifiers) for this media (32-character hex strings).
     */
    protected List<String> redirects;

    /**
     * Popularity of this media (from 0 to 100).
     */
    protected int popularity;

    /**
     * Restrictions of this media.
     */
    private List<Restriction> restrictions;

    /**
     * External ids of this media.
     */
    private Map<String, String> externalIds;

    /**
     * Creates an empty {@link Media} object.
     */
    protected Media()
    {
        this.id = null;
        this.redirects = new ArrayList<String>();
        this.popularity = -1;
        this.restrictions = new ArrayList<Restriction>();
        this.externalIds = new HashMap<String, String>();
    }

    /**
     * Creates a {@link Media} object with the specified {@code id}.
     *
     * @param id A 32-character hex string or a Spotify URI.
     * @throws IllegalArgumentException If the given id is invalid.
     */
    protected Media(String id)
    {
        this();

        /* Check if id is a 32-character hex string. */
        if (id.length() == 32 && Hex.isHex(id))
        {
            this.id = id;
        }
        /* Otherwise try to parse it as a Spotify URI. */
        else
        {
            try
            {
                this.id = Link.create(id).getId();
            }
            catch (Link.InvalidSpotifyURIException e)
            {
                throw new IllegalArgumentException(
                        "Given id is neither a 32-character " +
                                "hex string nor a valid Spotify URI.", e
                );
            }
        }

    }

    /**
     * Get the media identifier.
     *
     * @return A 32-character identifier.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Set the media identifier.
     *
     * @param id A 32-character identifier.
     * @throws IllegalArgumentException If the given id is invalid.
     */
    public void setId(String id)
    {
        /* Check if id string is valid. */
        if (id == null || id.length() != 32 || !Hex.isHex(id))
        {
            throw new IllegalArgumentException("Expecting a 32-character hex string.");
        }

        this.id = id;
    }

    /**
     * Get the media redirects.
     *
     * @return A {@link List} of 32-character identifiers.
     */
    public List<String> getRedirects()
    {
        return this.redirects;
    }

    /**
     * Add a media redirect.
     *
     * @param redirect A 32-character identifier.
     */
    public void addRedirect(String redirect)
    {
        this.redirects.add(redirect);
    }

    /**
     * Get the media popularity.
     *
     * @return A decimal value between 0.0 and 1.0 or {@link Float#NaN} if not available.
     */
    public float getPopularity()
    {
        return this.popularity;
    }

    /**
     * Set the media popularity.
     *
     * @param popularity A decimal value between 0.0 and 1.0 or {@link Float#NaN}.
     * @throws IllegalArgumentException If the given popularity value is invalid.
     */
    public void setPopularity(int popularity)
    {
        /* Check if popularity value is valid. */
        if (popularity != -1 && (popularity < 0 || popularity > 1))
        {
            throw new IllegalArgumentException("Expecting a value from 0 to 100 or -1");
        }

        this.popularity = popularity;
    }

    /**
     * Get the media restrictions.
     *
     * @return A {@link List} of {@link Restriction} objects.
     */
    public List<Restriction> getRestrictions()
    {
        return this.restrictions;
    }

    /**
     * Check if the media is restricted for the given {@code country} and {@code catalogue}.
     *
     * @param country   A 2-letter country code.
     * @param catalogue The catalogue to check.
     * @return true if it is restricted, false otherwise.
     * @throws IllegalArgumentException If the given 2-letter country code is invalid.
     */
    public boolean isRestricted(String country, String catalogue)
    {
        if (country.length() != 2)
        {
            throw new IllegalArgumentException("Expecting a 2-letter country code!");
        }

        for (Restriction restriction : this.restrictions)
        {
            if (restriction.isCatalogue(catalogue) &&
                    (restriction.isForbidden(country) ||
                            !restriction.isAllowed(country)))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Set the media restrictions.
     *
     * @param restrictions A {@link List} of {@link Restriction} objects.
     */
    public void setRestrictions(List<Restriction> restrictions)
    {
        this.restrictions = restrictions;
    }

    /**
     * Get the media external identifiers.
     *
     * @return A {@link Map} of external services and their identifers for the media.
     */
    public Map<String, String> getExternalIds()
    {
        return this.externalIds;
    }

    /**
     * Get an external identifier for the specified {@code service}.
     *
     * @param service The service to get the identifer for.
     * @return An identifier or null if not available.
     */
    public String getExternalId(String service)
    {
        return this.externalIds.get(service);
    }

    /**
     * Set the media external identifiers.
     *
     * @param externalIds A {@link Map} of external services and their identifers for the media.
     */
    public void setExternalIds(Map<String, String> externalIds)
    {
        this.externalIds = externalIds;
    }
}
