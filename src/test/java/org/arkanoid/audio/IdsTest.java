package org.arkanoid.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdsTest {

    @Test
    void sfxId_filenameMapping() {
        assertEquals("paddle_hit.wav", SfxId.PADDLE_HIT.getFilename());
        assertEquals("collision.wav", SfxId.BRICK_BREAK.getFilename());
        assertEquals("item_collected.wav", SfxId.ITEM_COLLECTED.getFilename());
    }

    @Test
    void bgmId_filenameMapping() {
        assertEquals("menu_music.mp3", BgmId.MENU.getFilename());
        assertEquals("playing_music.mp3", BgmId.PLAYING.getFilename());
        assertEquals("ending_music.mp3", BgmId.ENDING.getFilename());
    }
}
