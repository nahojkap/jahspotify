package jahspotify;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public interface SearchListener
{
    public void tracksFound(Link... tracks);
    public void albumsFound(Link... albums);
    public void artistsFound(Link... artists);
}
