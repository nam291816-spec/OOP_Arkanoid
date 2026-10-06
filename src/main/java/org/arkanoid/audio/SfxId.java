package org.arkanoid.audio;

/**
 * Enumeration for sound effect identifiers.
 * Maps to the available SFX files in the resources.
 */
public enum SfxId {
    PADDLE_HIT("paddle_hit.wav"),
    BRICK_BREAK("collision.wav"),
    ITEM_COLLECTED("item_collected.wav");
    
    private final String filename;
    
    SfxId(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
}