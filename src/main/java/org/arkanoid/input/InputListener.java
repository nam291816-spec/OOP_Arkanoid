package org.arkanoid.input;

import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/**
 * A singleton class that listens to and manages keyboard input.
 * Tracks which keys are currently pressed during gameplay.
 *
 */
public class InputListener {
    
    /** The singleton instance of InputListener. */
    private static InputListener instance;
    
    /** Set of currently pressed keys. */
    private final Set<KeyCode> pressedKeys;
    /** Keys that transitioned to pressed since last frame. */
    private final Set<KeyCode> justPressedKeys;
    /** Keys that transitioned to released since last frame. */
    private final Set<KeyCode> justReleasedKeys;

    /**
     * Private constructor to prevent instantiation.
     * Initializes the set of pressed keys.
     */
    private InputListener() {
        this.pressedKeys = new HashSet<>();
        this.justPressedKeys = new HashSet<>();
        this.justReleasedKeys = new HashSet<>();
    }

    /**
     * Returns the singleton instance of InputListener.
     * Creates the instance if it doesn't exist.
     *
     * @return the singleton instance
     */
    public static InputListener getInstance() {
        if (instance == null) {
            instance = new InputListener();
        }
        return instance;
    }

    /**
     * Registers a key as pressed.
     *
     * @param keyCode the key code to register as pressed
     */
    public void pressKey(KeyCode keyCode) {
        // Only mark as just-pressed if it wasn't already pressed
        if (pressedKeys.add(keyCode)) {
            justPressedKeys.add(keyCode);
        }
    }

    /**
     * Registers a key as released.
     *
     * @param keyCode the key code to register as released
     */
    public void releaseKey(KeyCode keyCode) {
        // Only mark as just-released if it was pressed before
        if (pressedKeys.remove(keyCode)) {
            justReleasedKeys.add(keyCode);
        }
    }

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param keyCode the key code to check
     * @return true if the key is pressed, false otherwise
     */
    public boolean isKeyPressed(KeyCode keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Checks if a key was pressed this frame (edge-triggered).
     * Call {@link #endFrame()} once per game tick to clear transitions.
     *
     * @param keyCode the key code to check
     * @return true if the key transitioned from up to down since last frame
     */
    public boolean isJustPressed(KeyCode keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    /**
     * Checks if a key was released this frame (edge-triggered).
     * Call {@link #endFrame()} once per game tick to clear transitions.
     *
     * @param keyCode the key code to check
     * @return true if the key transitioned from down to up since last frame
     */
    public boolean isJustReleased(KeyCode keyCode) {
        return justReleasedKeys.contains(keyCode);
    }

    /**
     * Clears per-frame transition states. Invoke exactly once per game loop iteration
     * after input has been processed.
     */
    public void endFrame() {
        justPressedKeys.clear();
        justReleasedKeys.clear();
    }
}
