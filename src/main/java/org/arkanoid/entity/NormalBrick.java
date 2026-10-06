package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.arkanoid.graphics.RectangleRenderer;

/**
 * Represents a standard breakable brick in the Arkanoid game.
 * Extends Brick and defines visual appearance using either
 * a loaded texture or a fallback colored rectangle.
 * Has finite hit points and is destroyed when they reach zero.
 */
public class NormalBrick extends Brick {

    private final RectangleRenderer renderer;
    private Image image;

    /**
     * Constructs a NormalBrick with position, size, durability, and identifier.
     * Attempts to load the brick texture; falls back to solid color if failed.
     *
     * @param x         X coordinate of the top-left corner
     * @param y         Y coordinate of the top-left corner
     * @param width     width of the brick in pixels
     * @param height    height of the brick in pixels
     * @param hitPoints number of hits required to destroy the brick
     * @param id        unique string identifier (e.g., "NB")
     */
    public NormalBrick(double x, double y, int width, int height, int hitPoints, String id) {
        super(x, y, width, height, hitPoints, id);
        this.renderer = new RectangleRenderer((int) x ,(int) y, width, height, Color.GREEN);
        try {
            this.image = new Image(getClass().getResourceAsStream("/images/normal_brick.png"));
        } catch (Exception e) {
            System.err.println("Failed to load paddle.png: " + e.getMessage());
            this.image = null;
        }
    }

    /**
     * Renders the brick on the canvas.
     * Draws the texture if available and the brick is not destroyed.
     * Falls back to the solid green rectangle otherwise.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!this.isDestroyed()){
            if (image != null) {
                gc.drawImage(image, getPointX(), getPointY(), getWidth(), getHeight());
            } else {
                renderer.render(gc);
            }
        }
    }

    @Override
    public void update(double deltaTime) {

    }
}
