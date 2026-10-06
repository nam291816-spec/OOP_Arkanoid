package org.arkanoid.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

/**
 * Manages background music playback.
 * Supports single active player with optional looping.
 */
public class BgmPlayer implements IBgmPlayer {
    private static final Logger logger = LogManager.getLogger(BgmPlayer.class);
    private MediaPlayer current;
    private BgmId currentId;
    private final ResourceLocator resourceLocator;
    private boolean isLooping = false;
    
    /**
     * Creates a new BgmPlayer.
     * 
     * @param resourceLocator The resource locator for loading BGM files
     */
    public BgmPlayer(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
    
    /**
     * Plays the specified background music.
     * 
     * @param id The BGM identifier
     * @param loop Whether to loop the music
     */
    public void play(BgmId id, boolean loop) {
        if (currentId == id && current != null && 
            current.getStatus() == MediaPlayer.Status.PLAYING) {
            logger.info("BGM is already playing: {}", id);
            return;
        }

        // Stop current playback if any
        stop();
        
        try {
            URL resourceUrl = resourceLocator.bgmResourceUrl(id);
            Media media = new Media(resourceUrl.toString());
            current = new MediaPlayer(media);
            currentId = id;
            isLooping = loop;
            
            if (loop) {
                current.setOnEndOfMedia(() -> {
                    if (isLooping && current != null) {
                        current.seek(javafx.util.Duration.ZERO);
                        current.play();
                    }
                });
            } else {
                current.setOnEndOfMedia(() -> {
                    current = null;
                    currentId = null;
                });
            }
            
            current.setOnError(() -> {
                logger.error("Error playing BGM: {}", id, current.getError());
                current = null;
                currentId = null;
            });
            
            current.play();
            
        } catch (Exception e) {
            logger.error("Failed to play BGM {}: {}", id, e.getMessage());
            current = null;
            currentId = null;
        }
    }
    
    /**
     * Pauses the current background music playback.
     */
    public void pause() {
        if (current != null) {
            current.pause();
        }
    }
    
    /**
     * Resumes the paused background music playback.
     */
    public void resume() {
        if (current != null) {
            current.play();
        }
    }
    
    /**
     * Stops the current background music playback.
     */
    public void stop() {
        if (current != null) {
            current.stop();
            current.dispose();
            current = null;
            currentId = null;
            isLooping = false;
        }
    }
    
    /**
     * Sets the volume for the current background music.
     * 
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        if (current != null) {
            current.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
    
    /**
     * Gets the currently playing BGM ID.
     * 
     * @return The current BGM ID, or null if not playing
     */
    public BgmId getCurrentId() {
        return currentId;
    }
    
    /**
     * Checks if background music is currently playing.
     * 
     * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        return current != null && 
               current.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    /**
     * Checks if background music is currently paused.
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return current != null && 
               current.getStatus() == MediaPlayer.Status.PAUSED;
    }
    
    /**
     * Checks if the current BGM is set to loop.
     * 
     * @return true if looping, false otherwise
     */
    public boolean isLooping() {
        return isLooping;
    }
    
    /**
     * Disposes of the BGM player resources.
     */
    public void dispose() {
        stop();
    }
}