package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.arkanoid.core.App;
import org.arkanoid.core.GameStateManager;

/**
 * Toggle button that switches between Pause and Play icons based on game state.
 */
public class PauseButton extends ImageButton {

    private final GameStateManager stateManager;

    /**
     * Creates a small Pause/Play button in the top-right corner.
     *
     * @param stateManager reference to manage PLAYING/PAUSED state
     */
    public PauseButton(GameStateManager stateManager) {
        super(App.WIDTH - 40, 5, 30, 30,
               "/images/btn_pause.png",
                "/images/btn_pause_hover.png"
        );
        this.stateManager = stateManager;
    }

    /**
     * Toggles between PLAYING and PAUSED game states on click.
     */
    @Override
    public void onClick() {
        if (stateManager.getCurrentState() == GameStateManager.GameState.PLAYING) {
            stateManager.setState(GameStateManager.GameState.PAUSED);
        } else if (stateManager.getCurrentState() == GameStateManager.GameState.PAUSED) {
            stateManager.setState(GameStateManager.GameState.PLAYING);
        }
    }

    /**
     * Dynamically updates button images based on current game state.
     *
     * @param gc the GraphicsContext to render on
     */
    @Override
    public void render(GraphicsContext gc) {
        // Determine correct image paths based on game state
        String normalPath = stateManager.getCurrentState() == GameStateManager.GameState.PLAYING
                ? "/images/btn_pause.png"           // Show PAUSE icon when playing
                : "/images/btn_play.png";           // Show PLAY icon when paused

        String hoverPath = stateManager.getCurrentState() == GameStateManager.GameState.PLAYING
                ? "/images/btn_pause_hover.png"
                : "/images/btn_play_hover.png";

        // Reload images every frame to reflect state changes
        try {
            this.normalImage = new Image(getClass().getResourceAsStream(normalPath));
            this.hoverImage = new Image(getClass().getResourceAsStream(hoverPath));
        } catch (Exception e) {
            // Silently ignore loading errors during runtime
        }

        // Delegate to parent class to draw the image with hover/glow
        super.render(gc);
    }

    /**
     * Button is visible only during gameplay (PLAYING or PAUSED).
     *
     * @param currentState current game state
     * @return true if in PLAYING or PAUSED
     */
    @Override
    public boolean shouldRender(GameStateManager.GameState currentState) {
        return currentState == GameStateManager.GameState.PLAYING ||
                currentState == GameStateManager.GameState.PAUSED;
    }
}