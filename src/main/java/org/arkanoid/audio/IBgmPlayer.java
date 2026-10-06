package org.arkanoid.audio;

/**
 * Abstraction for background music playback.
 * Implementations manage a single active track with optional looping and runtime state.
 */
public interface IBgmPlayer {
    /**
     * Play the specified background music.
     *
     * @param id   BGM identifier
     * @param loop whether the track should loop
     */
    void play(BgmId id, boolean loop);

    /** Pause current playback, if any. */
    void pause();

    /** Resume playback, if paused. */
    void resume();

    /** Stop playback and release the current track. */
    void stop();

    /**
     * Set the music volume.
     *
     * @param volume volume in range [0..1]
     */
    void setVolume(float volume);

    /**
     * @return the currently playing BGM id, or null if none.
     */
    BgmId getCurrentId();

    /** @return true if music is currently playing. */
    boolean isPlaying();

    /** @return true if music is currently paused. */
    boolean isPaused();

    /** @return true if the current track is set to loop. */
    boolean isLooping();

    /** Release resources held by the player. */
    void dispose();
}
