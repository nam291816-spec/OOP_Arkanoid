package org.arkanoid.input;

import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.GraphicsContext;
import org.arkanoid.core.App;
import org.arkanoid.core.GameStateManager;
import org.arkanoid.ui.Clickable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles mouse input for all clickable UI elements.
 * Manages click detection, hover feedback, and conditional rendering.
 */
public class MouseHandler {
    private final List<Clickable> buttons = new ArrayList<>();
    private final App app = App.getInstance();

    /**
     * Registers a new clickable UI element.
     *
     * @param button the Clickable element to add
     */
    public void addButton(Clickable button) {
        buttons.add(button);
    }

    /**
     * Processes a mouse click event and triggers the first matching button.
     *
     * @param event the MouseEvent containing click coordinates
     */
    public void handleClick(MouseEvent event) {
        for (Clickable b : buttons) {
            if (b.contains(event.getX(), event.getY())) {
                b.onClick();
                return;
            }
        }
    }

    /**
     * Updates hover state for all buttons and changes cursor on hover.
     *
     * @param event the MouseEvent containing current mouse position
     */
    public void handleHover(MouseEvent event) {
        boolean anyHover = false;
        for (Clickable b : buttons) {
            boolean hover = b.contains(event.getX(), event.getY());
            b.onHover(hover);
            if (hover) anyHover = true;
        }
        app.getGraphicsContext().getCanvas().setCursor(
                anyHover ? javafx.scene.Cursor.HAND : javafx.scene.Cursor.DEFAULT
        );
    }

    /**
     * Renders only the buttons that are visible in the current game state.
     *
     * @param gc the GraphicsContext to draw on
     * @param currentState the current GameStateManager.GameState
     */
    public void render(GraphicsContext gc, GameStateManager.GameState currentState) {
        for (Clickable btn : buttons) {
            if (btn.shouldRender(currentState)) {
                btn.render(gc);
            }
        }
    }
}