package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import org.arkanoid.core.GameStateManager;

/**
 * Interface for interactive on-screen elements that respond to mouse input.
 */
public interface Clickable {
    boolean contains(double x, double y);     // Checks whether the given point (x, y) lies within the clickable area.
    void onClick();                           // Executes the action associated with clicking this element.
    void render(GraphicsContext gc);
    void onHover(boolean isHover);            // Hover effect
    boolean shouldRender(GameStateManager.GameState currentState); // Determines visibility based on game state.
}