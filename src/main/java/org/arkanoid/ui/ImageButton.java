package org.arkanoid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import org.arkanoid.core.GameStateManager;

/**
 * Abstract base class for image-based UI buttons with hover glow effect.
 * Provides rendering, hover detection, and conditional visibility.
 */
public abstract class ImageButton implements Clickable {
    // Button position and size (in pixels)
    protected final int x, y, width, height;

    protected Image normalImage;
    protected Image hoverImage;

    // Tracks whether the mouse is currently over the button
    protected boolean isHovered = false;

    // Glow effect applied when the button is hovered
    private final DropShadow glow;

    /**
     * Constructs a button with position, size, and image resources.
     *
     * @param x          top-left X coordinate
     * @param y          top-left Y coordinate
     * @param width      button width in pixels
     * @param height     button height in pixels
     * @param normalPath path to normal state image (e.g., "/images/btn.png")
     * @param hoverPath  path to hover state image
     */
    public ImageButton(int x, int y, int width, int height, String normalPath, String hoverPath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Load images from the classpath (resources folder)
        try {
            this.normalImage = new Image(getClass().getResourceAsStream(normalPath));
            this.hoverImage = new Image(getClass().getResourceAsStream(hoverPath));
        } catch (Exception e) {
            System.err.println("Failed to load button images: " + e.getMessage());
        }

        // Create a yellow glow effect for hover feedback
        this.glow = new DropShadow(1, Color.YELLOW);
        this.glow.setOffsetX(0);
        this.glow.setOffsetY(0);
    }

    /**
     * Checks if a point is within the button bounds.
     *
     * @param px X coordinate to test
     * @param py Y coordinate to test
     * @return true if point is inside the button
     */
    @Override
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    /**
     * Updates hover state for visual feedback.
     *
     * @param isHover true if mouse is over the button
     */
    @Override
    public void onHover(boolean isHover) {
        this.isHovered = isHover;
    }

    /**
     * Renders the button using normal or hover image with glow effect.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        // Choose image based on hover state
        Image img = isHovered ? hoverImage : normalImage;

        // Draw the base image
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        }

        // Apply glow effect on top when hovered
        if (isHovered && img != null) {
            gc.save();                    // Save current graphics state
            gc.setEffect(glow);           // Apply glow
            gc.drawImage(img, x, y, width, height);
            gc.restore();                 // Restore state to avoid affecting other drawings
        }
    }

    // Abstract method: Subclasses must define what happens on click
    @Override
    public abstract void onClick();

    @Override
    public boolean shouldRender(GameStateManager.GameState currentState) {
        return true; // Visible by default
    }
}