package jahspotify;

/** Defines the method used for callbacks upon successful search completion.
 * @author Johan Lindquist
 */
public interface SearchListener
{
    public void searchComplete(SearchResult searchResult);
}