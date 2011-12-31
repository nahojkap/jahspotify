package jahspotify.services;

/** Listener interface to the <code>MediaPlayer</code>.  This provides callbacks on various events
 * raised by the player (track start, end, pause, etc) as well as a callback for providing the
 * next track to play.
 *
 * @author Johan Lindquist
 */
public interface MediaPlayerListener
{
    /** Signals a track started to play
     *
     * @param queueTrack
     */
    public void trackStart(QueueTrack queueTrack);

    /** Signal that a track ended (completed play).  The <code>forcedEnd</code> flag will indicate
     * whether the track completed naturally (ended) or was skipped by an action.
     *
     * @param queueTrack
     * @param forcedEnd
     */
    public void trackEnd(QueueTrack queueTrack, boolean forcedEnd);

    /** Signals a track was paused while playing
     *
     * @param currentTrack
     */
    public void paused(QueueTrack currentTrack);

    /** Signals play was resumed of a previously paused track
     *
     * @param currentTrack
     */
    public void resume(QueueTrack currentTrack);

    /** Signals the current track was skipped due to some action
     *
     * @param currentTrack
     * @param nextTrack
     */
    public void skip(QueueTrack currentTrack, QueueTrack nextTrack);

    /** Callback used when a new track is required for the media player.  There may be multiple source
     * providing the next track to play and if more than one source provide the next track, the track
     * to play is determined by the weight associated with it.
     *
     * @return
     */
    public QueueNextTrack nextTrackToQueue();

    public void stopped(QueueTrack currentTrack);

    public void seek(QueueTrack currentTrack, int offset);
}
