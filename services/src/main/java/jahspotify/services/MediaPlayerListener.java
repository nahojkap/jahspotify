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
     * @param currentTrack Track currently playing
     */
    public void trackStart(QueueTrack currentTrack);

    /** Signal that a track ended (completed play).  The <code>forcedEnd</code> flag will indicate
     * whether the track completed naturally (ended) or was skipped by an action.
     *
     * @param currentTrack Track currently playing
     * @param forcedEnd If true, the track did not complete naturally but was forced by an action
     */
    public void trackEnd(QueueTrack currentTrack, boolean forcedEnd);

    /** Signals a track was paused while playing
     *
     * @param currentTrack Track currently playing
     */
    public void paused(QueueTrack currentTrack);

    /** Signals play was resumed of a previously paused track
     *
     * @param currentTrack Track currently playing
     */
    public void resume(QueueTrack currentTrack);

    /** Signals the current track was skipped due to some action
     *
     * @param currentTrack Track currently playing
     * @param nextTrack Track to be played next
     */
    public void skip(QueueTrack currentTrack, QueueTrack nextTrack);

    /** Callback used when a new track is required for the media player.  There may be multiple source
     * providing the next track to play and if more than one source provide the next track, the track
     * to play is determined by the weight associated with it.
     *
     * @return
     * @deprecated
     */
    public QueueNextTrack nextTrackToQueue();

    /** Signals current playback was stopped.  Track can be restarted by starting play again
     *
     * @param currentTrack Track currently playing
     */
    public void stopped(QueueTrack currentTrack);

    /** Signals that a seek was performed within the currently playing track
     *
     * @param currentTrack Track currently playing
     * @param offset Offset to seek into the file, in seconds
     */
    public void seek(QueueTrack currentTrack, int offset);
}
