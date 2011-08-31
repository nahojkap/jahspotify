package jahspotify.impl;

import jahspotify.media.*;

/**
 * @author Johan Lindquist
 */
public interface NativeMediaLoadedListener
{
    public void track(final int token, final Link link);
    public void playlist(int token, final Link link);
    public void album(final int token, final Link link);
    public void image(final int token, final Link link);
    public void artist(final int token, final Link link);
}
