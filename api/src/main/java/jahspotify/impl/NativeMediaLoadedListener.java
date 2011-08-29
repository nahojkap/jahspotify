package jahspotify.impl;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface NativeMediaLoadedListener
{
    public void track(final Link link);
    public void playlist(int token, final Link link);
    public void album(final Link link);
    public void image(final Link link);
    public void artist(final Link link);
}
