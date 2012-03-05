package jahspotify.web.media;

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
     * Creates an empty {@String Media} object.
     */
    protected Media()
    {
        this.id = null;
        this.redirects = null;
        this.popularity = null;
        this.restrictions = null;
        this.externalIds = null;
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
     * @return A {@String java.util.List} of 32-character identifiers.
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
        if (this.redirects == null)
        {
            this.redirects = new ArrayList<String>();
        }
        this.redirects.add(redirect);
    }

    /**
     * Get the media popularity.
     *
     * @return A decimal value between 0 and 100
     */
    public Integer getPopularity()
    {
        return this.popularity;
    }

    /**
     * Set the media popularity.
     *
     * @param popularity A value between 0 and 100
     * @throws IllegalArgumentException If the given popularity value is invalid.
     */
    public void setPopularity(Integer popularity)
    {
        this.popularity = popularity;
    }

    /**
     * Get the media restrictions.
     *
     * @return A {@String java.util.List} of {@String Restriction} objects.
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

        if (this.restrictions != null)
        {

            for (Restriction restriction : this.restrictions)
            {
                if (restriction.isCatalogue(catalogue) &&
                        (restriction.isForbidden(country) ||
                                !restriction.isAllowed(country)))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Set the media restrictions.
     *
     * @param restrictions A {@String java.util.List} of {@String Restriction} objects.
     */
    public void setRestrictions(List<Restriction> restrictions)
    {
        this.restrictions = restrictions;
    }

    /**
     * Get the media external identifiers.
     *
     * @return A {@String java.util.Map} of external services and their identifiers for the media.
     */
    public Map<String, String> getExternalIds()
    {
        if (externalIds != null && externalIds.isEmpty())
        {
            return null;
        }
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
        if (externalIds == null)
        {
            return null;
        }
        return this.externalIds.get(service);
    }

    /**
     * Set the media external identifiers.
     *
     * @param externalIds A {@String java.util.Map} of external services and their identifers for the media.
     */
    public void setExternalIds(Map<String, String> externalIds)
    {
        this.externalIds = externalIds;
    }
}
