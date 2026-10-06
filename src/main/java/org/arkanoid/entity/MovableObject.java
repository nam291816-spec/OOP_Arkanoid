package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for game objects that can move (e.g., Ball, Paddle, Items).
 * Extends GameObject to add velocity (dx, dy) and speed.
 * Normalizes direction vectors and applies movement in {@code update()}.
 * Subclasses must implement render().
 */
public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;
    protected double speed;

    /**
     * Constructs a MovableObject with position and size.
     * Velocity defaults to zero.
     *
     * @param pointX X coordinate of the top-left corner
     * @param pointY Y coordinate of the top-left corner
     * @param width  width of the object in pixels
     * @param height height of the object in pixels
     */
    public MovableObject(double pointX, double pointY, int width, int height) {
        super(pointX, pointY, width, height);
    }

    /**
     * Constructs a MovableObject with position, size, and initial direction.
     * The direction vector is normalized automatically.
     *
     * @param PointX X coordinate of the top-left corner
     * @param PointY Y coordinate of the top-left corner
     * @param width  width of the object in pixels
     * @param height height of the object in pixels
     * @param dx     initial horizontal direction (raw value)
     * @param dy     initial vertical direction (raw value)
     */
    public MovableObject(double PointX, double PointY, int width, int height, double dx, double dy) {
        super(PointX, PointY, width, height);
        this.setDirection(dx, dy);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDx(double x) {
        this.dx = x;
    }

    public void  setDy(double dy) {
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    /**
     * Sets the movement direction using a raw vector.
     * The vector is normalized to unit length.
     * If both components are zero, direction becomes (0,0).
     *
     * @param dx raw horizontal component
     * @param dy raw vertical component
     */
    public void setDirection(double dx, double dy) {
        double len =  Math.sqrt(dx * dx + dy * dy);

        if (len != 0) {
            this.dx = dx / len;
            this.dy = dy / len;
        } else {
            this.dx = 0;
            this.dy = 0;
        }
    }

    public void reverseX() {
        this.dx *= -1;
    }

    public void reverseY() {
        this.dy *= -1;
    }

    public void update(double deltaTime) {
        this.setPointX(this.getPointX() + dx * speed * deltaTime);
        this.setPointY(this.getPointY() + dy * speed * deltaTime);
    };

    /**
     * Renders the object on the canvas.
     * Must be implemented by subclasses to define visual appearance.
     *
     * @param gc the GraphicsContext used for drawing
     */
    public abstract void render(GraphicsContext gc);
}