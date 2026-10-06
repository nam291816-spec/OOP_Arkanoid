package org.arkanoid.audio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AudioManagerTest {

    @Mock
    private ISfxPlayer mockSfxPlayer;
    
    @Mock
    private IBgmPlayer mockBgmPlayer;
    
    @Mock
    private ResourceLocator mockResourceLocator;
    
    private AudioManager audioManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up default mock behavior
        when(mockSfxPlayer.getVolume()).thenReturn(1.0f);
        when(mockBgmPlayer.getCurrentId()).thenReturn(null);
        when(mockBgmPlayer.isPlaying()).thenReturn(false);
        
        audioManager = new AudioManager(mockSfxPlayer, mockBgmPlayer, mockResourceLocator);
    }

    @Test
    void constructor_injection_createsAudioManagerWithDependencies() {
        assertNotNull(audioManager);
        assertEquals(1.0f, audioManager.getSfxVolume(), 1e-6);
        assertEquals(1.0f, audioManager.getBgmVolume(), 1e-6);
        assertNull(audioManager.getCurrentBgm());
        assertFalse(audioManager.isBgmPlaying());
    }

    @Test
    void defaults_areSane_withConstructorInjection() {
        assertEquals(1.0f, audioManager.getSfxVolume(), 1e-6);
        assertEquals(1.0f, audioManager.getBgmVolume(), 1e-6);
        assertNull(audioManager.getCurrentBgm());
        assertFalse(audioManager.isBgmPlaying());
        audioManager.dispose();
    }

    @Test
    void bgmVolume_isClamped() {
        audioManager.setBgmVolume(-0.25f);
        assertEquals(0.0f, audioManager.getBgmVolume(), 1e-6);

        audioManager.setBgmVolume(0.42f);
        assertEquals(0.42f, audioManager.getBgmVolume(), 1e-6);

        audioManager.setBgmVolume(2.0f);
        assertEquals(1.0f, audioManager.getBgmVolume(), 1e-6);

        audioManager.dispose();
    }

    @Test
    void bgm_controls_areNoOp_whenNotPlaying() {
        // Should not throw when nothing is playing
        audioManager.pauseBgm();
        audioManager.resumeBgm();
        audioManager.stopBgm();
        audioManager.dispose();
        
        // Verify interactions with mock BGM player
        verify(mockBgmPlayer).pause();
        verify(mockBgmPlayer).resume();
        verify(mockBgmPlayer).stop();
    }

    @Test
    void playSfx_delegatesToSfxPlayer() {
        audioManager.playSfx(SfxId.PADDLE_HIT);
        verify(mockSfxPlayer).play(SfxId.PADDLE_HIT, 1.0f, 0.0f);
    }

    @Test
    void playSfx_withVolumeAndPan_delegatesToSfxPlayer() {
        audioManager.playSfx(SfxId.BRICK_BREAK, 0.8f, 0.5f);
        verify(mockSfxPlayer).play(SfxId.BRICK_BREAK, 0.8f, 0.5f);
    }

    @Test
    void preloadSfx_delegatesToSfxPlayer() {
        audioManager.preloadSfx(SfxId.ITEM_COLLECTED);
        verify(mockSfxPlayer).preload(SfxId.ITEM_COLLECTED);
    }

    @Test
    void setSfxVolume_delegatesToSfxPlayer() {
        audioManager.setSfxVolume(0.7f);
        verify(mockSfxPlayer).setVolume(0.7f);
    }

    @Test
    void playBgm_delegatesToBgmPlayer() {
        audioManager.playBgm(BgmId.MENU);
        verify(mockBgmPlayer).play(BgmId.MENU, true);
        verify(mockBgmPlayer).setVolume(1.0f); // Default BGM volume
    }

    @Test
    void playBgm_withLoop_delegatesToBgmPlayer() {
        audioManager.playBgm(BgmId.PLAYING, false);
        verify(mockBgmPlayer).play(BgmId.PLAYING, false);
        verify(mockBgmPlayer).setVolume(1.0f);
    }

    @Test
    void bgmControls_delegateToBgmPlayer() {
        audioManager.pauseBgm();
        audioManager.resumeBgm();
        audioManager.stopBgm();
        
        verify(mockBgmPlayer).pause();
        verify(mockBgmPlayer).resume();
        verify(mockBgmPlayer).stop();
    }

    @Test
    void setBgmVolume_updatesInternalVolumeAndBgmPlayer() {
        audioManager.setBgmVolume(0.6f);
        assertEquals(0.6f, audioManager.getBgmVolume(), 1e-6);
        verify(mockBgmPlayer).setVolume(0.6f);
    }

    @Test
    void dispose_delegatesToPlayers() {
        audioManager.dispose();
        verify(mockSfxPlayer).dispose();
        verify(mockBgmPlayer).dispose();
    }

    @Test
    void deprecatedConstructor_stillWorks() {
        // Test that the deprecated constructor still works for backward compatibility
        AudioManager deprecatedAudioManager = new AudioManager();
        assertNotNull(deprecatedAudioManager);
        assertEquals(1.0f, deprecatedAudioManager.getSfxVolume(), 1e-6);
        assertEquals(1.0f, deprecatedAudioManager.getBgmVolume(), 1e-6);
        deprecatedAudioManager.dispose();
    }
}
