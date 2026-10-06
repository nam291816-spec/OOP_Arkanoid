package org.arkanoid.audio;

/**
 * Main audio management class that coordinates SFX and BGM playback.
 * Provides a unified interface for all audio operations in the game.
 */
public class AudioManager {
    private final ISfxPlayer sfxPlayer;
    private final IBgmPlayer bgmPlayer;
    private final ResourceLocator resourceLocator;
    private float bgmVolume = 1.0f;
    
    /**
     * Creates a new AudioManager with dependency injection.
     *
     * @param sfxPlayer The sound effects player implementation
     * @param bgmPlayer The background music player implementation
     * @param resourceLocator The resource locator for audio assets
     */
    public AudioManager(ISfxPlayer sfxPlayer, IBgmPlayer bgmPlayer, ResourceLocator resourceLocator) {
        this.sfxPlayer = sfxPlayer;
        this.bgmPlayer = bgmPlayer;
        this.resourceLocator = resourceLocator;
    }
    
    /**
     * Creates a new AudioManager with default settings.
     * @deprecated Use constructor injection instead for better testability and flexibility
     */
    @Deprecated
    public AudioManager() {
        this.resourceLocator = new ResourceLocator();
        this.sfxPlayer = new SfxPlayer(resourceLocator);
        this.bgmPlayer = new BgmPlayer(resourceLocator);
    }
    
    /**
     * Plays a sound effect with specified volume and panning.
     *
     * @param id The SFX identifier
     * @param vol Volume level (0.0 to 1.0, default 1.0)
     * @param pan Pan value (-1.0 left to 1.0 right, default 0.0)
     */
    public void playSfx(SfxId id, float vol, float pan) {
        sfxPlayer.play(id, vol, pan);
    }
    
    /**
     * Plays a sound effect with default volume and panning.
     *
     * @param id The SFX identifier
     */
    public void playSfx(SfxId id) {
        playSfx(id, 1.0f, 0.0f);
    }
    
    /**
     * Preloads a sound effect.
     * 
     * @param id The SFX identifier
     */
    public void preloadSfx(SfxId id) {
        sfxPlayer.preload(id);
    }
    
    /**
     * Acquires an audio clip for the specified sound effect.
     * Auto-loads if not already preloaded.
     *
     * @param id The SFX identifier
     * @return AudioClip for the sound effect, or null if loading failed
     */
    // SFX clip acquisition is handled by SfxPlayer now.
    
    /**
     * Sets the master volume for all sound effects.
     * 
     * @param v Volume level (0.0 to 1.0)
     */
    public void setSfxVolume(float v) {
        sfxPlayer.setVolume(v);
    }
    
    /**
     * Plays background music with optional looping.
     * 
     * @param id The BGM identifier
     * @param loop Whether to loop the music (default true)
     */
    public void playBgm(BgmId id, boolean loop) {
        bgmPlayer.play(id, loop);
        bgmPlayer.setVolume(bgmVolume);
    }
    
    /**
     * Plays background music with looping enabled by default.
     * 
     * @param id The BGM identifier
     */
    public void playBgm(BgmId id) {
        playBgm(id, true);
    }
    
    /**
     * Pauses the current background music playback.
     */
    public void pauseBgm() {
        bgmPlayer.pause();
    }
    
    /**
     * Resumes the paused background music playback.
     */
    public void resumeBgm() {
        bgmPlayer.resume();
    }
    
    /**
     * Stops the current background music playback.
     */
    public void stopBgm() {
        bgmPlayer.stop();
    }
    
    /**
     * Sets the master volume for background music.
     * 
     * @param v Volume level (0.0 to 1.0)
     */
    public void setBgmVolume(float v) {
        this.bgmVolume = Math.max(0.0f, Math.min(1.0f, v));
        bgmPlayer.setVolume(this.bgmVolume);
    }
    
    /**
     * Gets the current SFX volume level.
     * 
     * @return Current SFX volume (0.0 to 1.0)
     */
    public float getSfxVolume() {
        return sfxPlayer.getVolume();
    }
    
    /**
     * Gets the current BGM volume level.
     * 
     * @return Current BGM volume (0.0 to 1.0)
     */
    public float getBgmVolume() {
        return bgmVolume;
    }
    
    /**
     * Gets the currently playing BGM ID.
     * 
     * @return Current BGM ID, or null if not playing
     */
    public BgmId getCurrentBgm() {
        return bgmPlayer.getCurrentId();
    }
    
    /**
     * Checks if background music is currently playing.
     * 
     * @return true if playing, false otherwise
     */
    public boolean isBgmPlaying() {
        return bgmPlayer.isPlaying();
    }
    
    /**
     * Disposes of all audio resources.
     * Should be called when the application is shutting down.
     */
    public void dispose() {
        if (sfxPlayer != null) {
            sfxPlayer.dispose();
        }
        if (bgmPlayer != null) {
            bgmPlayer.dispose();
        }
    }
    
}