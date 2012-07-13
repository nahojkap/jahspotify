package jahspotify.media;

import java.util.*;

/**
 * Holds basic information about media.
 *
 * @author Felix Bruns <felixbruns@web.de>
 */
public class Media
{
    /**
     * Identifier for this media object (32-character hex string).
     */
    protected Link id;

    /**
     * Date when this media object was considered to have been modified
     */
    private Date lastModified;

    /**
     * Redirects (other identifiers) for this media (32-character hex strings).
     */
    protected List<String> redirects;

    /**
     * Popularity of this media (from 0 to 100).
     */
    protected Integer popularity;

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
        this.restrictions = new ArrayList<Restriction>();
        this.externalIds = new HashMap<String, String>();
    }

    /**
     * Get the media identifier.
     *
     * @return A 32-character identifier.
     */
    public Link getId()
    {
        return this.id;
    }

    public void setId(Link id)
    {
        this.id = id;
    }

    public Date getLastModified()
    {
        if (lastModified == null)
        {
            lastModified = new Date();
        }
        return lastModified;
    }

    public void setLastModified(final Date lastModified)
    {
        this.lastModified = lastModified;
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
     * @return A value between 0 and 100
     */
    public Integer getPopularity()
    {
        return this.popularity;
    }

    /**
     * Set the media popularity.
     *
     * @param popularity A value between 0 and 100.
     */
    public void setPopularity(Integer popularity)
    {
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
