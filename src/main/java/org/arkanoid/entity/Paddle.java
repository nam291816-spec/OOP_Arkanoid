package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.arkanoid.core.App;
import org.arkanoid.graphics.RectangleRenderer;

/**
 * Represents the player-controlled paddle in the Arkanoid game.
 * Extends MovableObject to support horizontal movement.
 * Handles input-based movement, screen boundary clamping, and visual rendering
 * using either a loaded texture or a fallback colored rectangle.
 */
public class Paddle extends MovableObject {
    public static final int ORIGINAL_WIDTH = 80;

    private boolean moving;
    private boolean movingRight;
    public final double startX = (double) (App.WIDTH / 2 - ORIGINAL_WIDTH / 2);
    public final double startY = (double) (App.HEIGHT - this.getHeight() / 2 - 10);

    private RectangleRenderer renderer;
    private Image image;

    /**
     * Constructs a Paddle with position and dimensions.
     * Speed defaults to 0 (set externally via input).
     *
     * @param x      X coordinate of the top-left corner
     * @param y      Y coordinate of the top-left corner
     * @param width  width of the paddle in pixels
     * @param height height of the paddle in pixels
     */
    public Paddle(double x, double y, int width, int height) {
        super(x, y, width, height);

        this.moving = false;
        this.renderer = new RectangleRenderer(x, y, width, height, Color.BLUE);
        try {
            this.image = new Image(getClass().getResourceAsStream("/images/paddle.png"));
        } catch (Exception e) {
            System.err.println("Failed to load paddle.png: " + e.getMessage());
            this.image = null;
        }
    }

    /**
     * Constructs a Paddle with position, dimensions, and movement speed.
     * Direction is initialized to zero.
     *
     * @param x      X coordinate of the top-left corner
     * @param y      Y coordinate of the top-left corner
     * @param width  width of the paddle in pixels
     * @param height height of the paddle in pixels
     * @param speed  movement speed in pixels per second
     */
    public Paddle(double x, double y, int width, int height, double speed) {
        super(x, y, width, height);
        this.setSpeed(speed);
        this.setDirection(0, 0);

        this.moving = false;
        this.renderer = new RectangleRenderer(x, y, width, height, Color.BLUE);
        try {
            this.image = new Image(getClass().getResourceAsStream("/images/paddle.png"));
        } catch (Exception e) {
            System.err.println("Cannot load paddle.png: " + e.getMessage());
            this.image = null;
        }
    }

    /**
     * Updates the paddle's movement state and position.
     * Applies input direction, clamps to screen edges, and updates renderer.
     *
     * @param deltaTime time elapsed since last frame in seconds
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (moving) {
            if (movingRight) {
                this.setDirection(1, 0);
            } else {
                this.setDirection(-1, 0);
            }
        } else {
            this.setDirection(0, 0);
        }

        // Limit the Paddle to the Screen
        if (pointX < 0) {
            pointX = 0;
        }

        if (pointX + width > App.WIDTH) {
            pointX = App.WIDTH - width;
        }

        renderer.setPosition(pointX, pointY, this.width, this.height);
    }

    /**
     * Renders the paddle on the canvas.
     * Uses texture if available; otherwise falls back to solid blue rectangle.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, getPointX(), getPointY(), getWidth(), getHeight());
        } else {
            renderer.render(gc);
        }
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void moveLeft() {
        movingRight = false;
        moving = true;
    }

    public void moveRight() {
        movingRight = true;
        moving = true;
    }

    public void stopMoving() {
        moving = false;
    }

    public void reset() {
        this.width = ORIGINAL_WIDTH;
        this.setPointX(startX);
        setPointY(startY);
        this.movingRight = true;
        renderer.setPosition(startX, startY, this.width, this.height);
    }
}
