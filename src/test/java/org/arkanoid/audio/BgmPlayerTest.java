package org.arkanoid.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BgmPlayerTest {

    @Test
    void initialState_noCurrentMedia() {
        BgmPlayer p = new BgmPlayer(new ResourceLocator());
        assertNull(p.getCurrentId());
        assertFalse(p.isPlaying());
        assertFalse(p.isPaused());
        assertFalse(p.isLooping());
    }

    @Test
    void safe_noOpMethods_whenNoMedia() {
        BgmPlayer p = new BgmPlayer(new ResourceLocator());

        // Should not throw when nothing is loaded
        p.pause();
        p.resume();
        p.stop();
        p.setVolume(0.5f);
        p.dispose();

        // state remains unchanged
        assertNull(p.getCurrentId());
        assertFalse(p.isPlaying());
    }
}
