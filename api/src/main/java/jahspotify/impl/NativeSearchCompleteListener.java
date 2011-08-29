package jahspotify.impl;

import jahspotify.*;

/**
 * @author Johan Lindquist
 */
public interface NativeSearchCompleteListener
{
    public void searchCompleted(int token, SearchResult searchResult);
}
