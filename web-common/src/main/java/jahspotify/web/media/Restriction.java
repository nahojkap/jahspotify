package jahspotify.web.media;

/**
 * Holds restriction information of media. Every operation in this class
 * automatically converts strings to lower-case before comparing with them.
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @category Media
 */
public class Restriction
{
    /**
     * A delimeter separated list of allowed 2-letter country codes.
     */
    private String allowed;

    /**
     * A delimeter separated list of forbidden 2-letter country codes.
     */
    private String forbidden;

    /**
     * A delimeter separated list of catalogues this restriction applies to.
     */
    private String catalogues;

    /**
     * Create an empty {@String Restriction} object.
     */
    public Restriction()
    {
        this.allowed = null;
        this.forbidden = null;
        this.catalogues = null;
    }

    /**
     * Create a restriction with the specified countries and catalogues.
     * A delimeter can be a comma or a space for example.
     *
     * @param allowed    A delimeter separated list of allowed 2-letter country codes.
     * @param forbidden  A delimeter separated list of forbidden 2-letter country codes.
     * @param catalogues A delimeter separated list of catalogues this restriction applies to.
     */
    public Restriction(String allowed, String forbidden, String catalogues)
    {
        this.allowed = allowed;
        this.forbidden = forbidden;
        this.catalogues = catalogues;
    }

    /**
     * Get a delimeter separated list of allowed 2-letter country codes.
     *
     * @return A string.
     */
    public String getAllowed()
    {
        return this.allowed;
    }

    /**
     * Check if a country is allowed by this restriction.
     *
     * @param country A 2-letter country code.
     * @return true if it is allowed, false otherwise.
     * @throws IllegalArgumentException If the given 2-letter country code is invalid.
     */
    public boolean isAllowed(String country)
    {
        if (country.length() != 2)
        {
            throw new IllegalArgumentException("Expecting a 2-letter country code!");
        }

        if (this.allowed == null)
        {
            return true;
        }

        return this.allowed.toLowerCase().contains(country.toLowerCase());
    }

    /**
     * Set a delimeter separated list of allowed 2-letter country codes.
     *
     * @param allowed A string.
     */
    public void setAllowed(String allowed)
    {
        this.allowed = allowed;
    }

    /**
     * Get a delimeter separated list of forbidden 2-letter country codes.
     *
     * @return A string.
     */
    public String getForbidden()
    {
        return this.forbidden;
    }

    /**
     * Check if a country is forbidden by this restriction.
     *
     * @param country A 2-letter country code.
     * @return true if it is forbidden, false otherwise.
     * @throws IllegalArgumentException If the given 2-letter country code is invalid.
     */
    public boolean isForbidden(String country)
    {
        if (country.length() != 2)
        {
            throw new IllegalArgumentException("Expecting a 2-letter country code!");
        }

        if (this.forbidden == null)
        {
            return false;
        }

        return this.forbidden.toLowerCase().contains(country.toLowerCase());
    }

    /**
     * Set a delimeter separated list of forbidden 2-letter country codes.
     *
     * @param forbidden A string.
     */
    public void setForbidden(String forbidden)
    {
        this.forbidden = forbidden;
    }

    /**
     * Get a delimeter separated list of catalogues this restriction applies to.
     *
     * @return A string.
     */
    public String getCatalogues()
    {
        return this.catalogues;
    }

    /**
     * Check if this restriction applies to a specified catalogue.
     *
     * @param catalogue A catalogue to test.
     * @return true if it applies, false otherwise.
     */
    public boolean isCatalogue(String catalogue)
    {
        return this.catalogues != null && this.catalogues.toLowerCase().contains(catalogue.toLowerCase());
    }

    /**
     * Set a delimeter separated list of catalogues this restriction applies to.
     *
     * @param catalogues A string.
     */
    public void setCatalogues(String catalogues)
    {
        this.catalogues = catalogues;
    }

    public String toString()
    {
        return String.format("[Restriction: %s, %s, %s]", this.catalogues, this.allowed, this.forbidden);
    }
}
