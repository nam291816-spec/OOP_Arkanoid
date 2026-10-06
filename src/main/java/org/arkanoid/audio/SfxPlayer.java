package org.arkanoid.audio;

import javafx.scene.media.AudioClip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles sound effect (SFX) loading and playback.
 * Maintains its own master volume and simple in-memory cache of AudioClips.
 */
public class SfxPlayer implements ISfxPlayer {
    private static final Logger logger = LogManager.getLogger(SfxPlayer.class);

    private final Map<SfxId, AudioClip> sfxClips = new HashMap<>();
    private final ResourceLocator resourceLocator;
    private float volume = 1.0f;

    /**
     * Create a new SfxPlayer.
     *
     * @param resourceLocator shared resource locator for resolving SFX assets
     */
    public SfxPlayer(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        preloadCommonSfx();
    }

    /**
     * Play a sound effect.
     *
     * @param id  SFX id
     * @param vol Per-play call volume (0..1). Will be multiplied with master volume.
     * @param pan Stereo pan [-1..1]
     */
    public void play(SfxId id, float vol, float pan) {
        float finalVolume = clamp01(vol) * clamp01(volume);
        AudioClip clip = acquireSfxClip(id);
        if (clip != null) {
            clip.setVolume(clamp01(finalVolume));
            clip.setPan(clamp(pan, -1.0, 1.0));
            clip.play();
        } else {
            logger.warn("Failed to play SFX: {}", id);
        }
    }

    /**
     * Play a sound effect with default volume and centered pan.
     */
    public void play(SfxId id) {
        play(id, 1.0f, 0.0f);
    }

    /**
     * Preload a single SFX clip.
     */
    public void preload(SfxId id) {
        if (!sfxClips.containsKey(id)) {
            try {
                URL resourceUrl = resourceLocator.sfxResourceUrl(id);
                AudioClip clip = new AudioClip(resourceUrl.toString());
                sfxClips.put(id, clip);
            } catch (Exception e) {
                logger.error("Failed to preload SFX {}: {}", id, e.getMessage());
            }
        }
    }

    /**
     * Set master SFX volume.
     */
    public void setVolume(float v) {
        this.volume = clamp01(v);
    }

    /**
     * Get master SFX volume.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Release cached resources.
     */
    public void dispose() {
        sfxClips.clear();
    }

    private AudioClip acquireSfxClip(SfxId id) {
        AudioClip clip = sfxClips.get(id);
        if (clip == null) {
            preload(id);
            clip = sfxClips.get(id);
        }
        return clip;
    }

    private void preloadCommonSfx() {
        preload(SfxId.PADDLE_HIT);
        preload(SfxId.BRICK_BREAK);
        preload(SfxId.ITEM_COLLECTED);
    }

    private static float clamp01(float v) {
        return (float) clamp(v, 0.0, 1.0);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
