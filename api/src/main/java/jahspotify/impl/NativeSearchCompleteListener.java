package jahspotify.impl;

import jahspotify.SearchListener;

/**
 * @author Johan Lindquist
 */
public interface NativeSearchCompleteListener
{
    public void searchCompleted(int token, SearchListener.SearchResult searchResult);
}
