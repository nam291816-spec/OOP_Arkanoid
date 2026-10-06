package org.arkanoid.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.input.KeyCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages keybinding configuration for the Arkanoid game.
 * Loads keybindings from a properties file or uses defaults if unavailable.
 */
public class Keybinds {

    private static final Logger logger = LogManager.getLogger(Keybinds.class);
    private static final Properties keybinds = new Properties();

    /** KeyCode for moving the paddle left. */
    public static KeyCode MOVE_LEFT, MOVE_RIGHT, PAUSE, EXIT, RESET, NEXT_STATE, LAUNCH_BALL;

    static {
        try (InputStream input = Keybinds.class.getResourceAsStream("/keybind.properties")) {
            if (input == null) {
                logger.warn("Keybind configuration file not found, using default keybinds.");
                setDefaultKeybinds();
            } else {
                keybinds.load(input);
                MOVE_LEFT = KeyCode.valueOf(keybinds.getProperty("move.left", "LEFT"));
                MOVE_RIGHT = KeyCode.valueOf(keybinds.getProperty("move.right", "RIGHT"));
                PAUSE = KeyCode.valueOf(keybinds.getProperty("pause", "P"));
                EXIT = KeyCode.valueOf(keybinds.getProperty("exit", "ESCAPE"));
                RESET = KeyCode.valueOf(keybinds.getProperty("reset", "R"));
                NEXT_STATE = KeyCode.valueOf(keybinds.getProperty("next.state", "ENTER"));
                LAUNCH_BALL = KeyCode.valueOf(keybinds.getProperty("launch.ball", "SPACE"));
            }
        } catch (IOException e) {
            logger.error("Error loading keybind configuration, using default keybinds.", e);
            setDefaultKeybinds();
        }
    }

    /**
     * Sets the default keybindings when configuration file is unavailable or invalid.
     */
    private static void setDefaultKeybinds() {
        MOVE_LEFT = KeyCode.LEFT;
        MOVE_RIGHT = KeyCode.RIGHT;
        PAUSE = KeyCode.P;
        EXIT = KeyCode.ESCAPE;
        RESET = KeyCode.R;
        NEXT_STATE = KeyCode.ENTER;
        LAUNCH_BALL = KeyCode.SPACE;
    }
  
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Keybinds() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}