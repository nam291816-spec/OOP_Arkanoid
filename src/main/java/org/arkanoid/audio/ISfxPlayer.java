package org.arkanoid.audio;

/**
 * Abstraction for playing short sound effects.
 * Implementations should be lightweight and support fire-and-forget playback.
 */
public interface ISfxPlayer {
    /**
     * Play a sound effect with default volume and centered pan.
     *
     * @param id SFX identifier
     */
    void play(SfxId id);

    /**
     * Play a sound effect.
     *
     * @param id  SFX identifier
     * @param vol Per-play call volume (0..1). Will be multiplied with master volume.
     * @param pan Stereo pan in range [-1..1]
     */
    void play(SfxId id, float vol, float pan);

    /**
     * Optionally preload a sound effect to reduce first-play latency.
     *
     * @param id SFX identifier to preload
     */
    void preload(SfxId id);

    /**
     * Set master SFX volume.
     *
     * @param v volume in range [0..1]
     */
    void setVolume(float v);

    /**
     * Get master SFX volume.
     *
     * @return current volume in range [0..1]
     */
    float getVolume();

    /**
     * Release any cached resources.
     */
    void dispose();
}
