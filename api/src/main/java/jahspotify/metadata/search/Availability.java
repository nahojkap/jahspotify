package jahspotify.metadata.search;

import java.util.Arrays;

/**
 * @author Johan Lindquist
 */
public class Availability
{
    String territories;

    public String getTerritories()
    {
        return territories;
    }

    public void setTerritories(final String territories)
    {
        this.territories = territories;
    }

    @Override
    public String toString()
    {
        return "Availability{" +
                "territories=" + (territories == null ? null : Arrays.asList(territories)) +
                '}';
    }
}
