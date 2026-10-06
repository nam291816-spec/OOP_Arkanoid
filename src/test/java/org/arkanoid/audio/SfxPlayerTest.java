package org.arkanoid.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lightweight tests for SfxPlayer focusing on internal logic that doesn't depend
 * on actual audio device behavior.
 */
public class SfxPlayerTest {

    @Test
    void volume_isClampedBetweenZeroAndOne() {
        SfxPlayer p = new SfxPlayer(new ResourceLocator());

        p.setVolume(-0.5f);
        assertEquals(0.0f, p.getVolume(), 1e-6);

        p.setVolume(0.5f);
        assertEquals(0.5f, p.getVolume(), 1e-6);

        p.setVolume(1.5f);
        assertEquals(1.0f, p.getVolume(), 1e-6);

        p.dispose();
    }

    @Test
    void dispose_isIdempotent() {
        SfxPlayer p = new SfxPlayer(new ResourceLocator());
        // Calling dispose multiple times should not throw
        p.dispose();
        p.dispose();
    }
}
