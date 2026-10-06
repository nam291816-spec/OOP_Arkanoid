package org.arkanoid.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract base class for all graphical renderers in the Arkanoid game.
 * Encapsulates position, size, and color properties used to draw fallback
 * shapes when textures fail to load or are unavailable.
 */
public abstract class Renderer {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Color color;

    /**
     * Constructs a Renderer with initial position, size, and color.
     *
     * @param x      X coordinate of the top-left corner
     * @param y      Y coordinate of the top-left corner
     * @param width  width of the shape in pixels
     * @param height height of the shape in pixels
     * @param color  fill color for the shape
     */
    public Renderer(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public abstract void render(GraphicsContext gc);

    public void setPosition(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}