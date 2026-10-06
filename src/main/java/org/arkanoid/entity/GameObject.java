package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all game objects in the Arkanoid game.
 * Provides common properties (position, size) and defines the contract
 * for updating and rendering. All visual and interactive entities
 * (Ball, Paddle, Brick, Item, etc.) extend this class.
 */
public abstract class GameObject {

    protected double pointX;
    protected double pointY;
    protected int width;
    protected int height;

    /**
     * Default constructor.
     * Initializes the object at position (0, 0) with zero dimensions.
     * Used when properties will be set later.
     */
    public GameObject() {
        pointX = 0;
        pointY = 0;
    }

    /**
     * Constructs a GameObject with specified position and dimensions.
     *
     * @param pointX X coordinate of the top-left corner
     * @param pointY Y coordinate of the top-left corner
     * @param width  width of the object in pixels
     * @param height height of the object in pixels
     */
    public GameObject(double pointX, double pointY, int width, int height) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.width = width;
        this.height = height;
    }

    public void  setPointX(double pointX) {
        this.pointX = pointX;
    }

    public double getPointX() {
        return this.pointX;
    }

    public double getPointY() {
        return pointY;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public abstract void update(double deltaTime);

    public abstract void render(GraphicsContext gc);
}
