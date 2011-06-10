package jahspotify;

/**
 * @author Johan Lindquist
 */
public interface PlaybackListener
{
    public void trackStarted(String uri);
    public void trackEnded(String uri, boolean forcedEnd);
    public String nextTrackToPreload();
}
