package jahspotify.impl;

/**
 * @author Johan Lindquist
 */
public interface NativePlaybackListener
{
    public void trackStarted(String uri);
    public void trackEnded(String uri, boolean forcedEnd);
    public String nextTrackToPreload();
}
