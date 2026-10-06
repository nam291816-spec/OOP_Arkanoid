package org.arkanoid.audio;

/**
 * Enumeration for background music identifiers.
 * Maps to the available BGM files in the resources.
 */
public enum BgmId {
    MENU("menu_music.mp3"),
    PLAYING("playing_music.mp3"),
    ENDING("ending_music.mp3");
    
    private final String filename;
    
    BgmId(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
}