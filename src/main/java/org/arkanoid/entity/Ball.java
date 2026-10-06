package org.arkanoid.entity;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.arkanoid.graphics.CircleRenderer;

/**
 * Represents the ball in the Arkanoid game.
 * Extends MovableObject to inherit position, velocity, and update logic.
 * Handles movement, collision response (bounce), visual hit effect, and reset behavior.
 */
public class Ball extends MovableObject {
    public static final int ORIGINAL_SPEED = 360;

    private double cx;
    private double cy;
    private boolean alive = true;
    private double radius;
    private boolean moving = false;

    private boolean isHit = false;
    private double hitTimer = 0;
    private static final double HIT_DURATION = 0.1;

    private final CircleRenderer renderer;
    private Image image;
    private Image hitImage;

    /**
     * Constructs a new Ball with specified center position and velocity.
     *
     * @param cx center X coordinate
     * @param cy center Y coordinate
     * @param r  radius of the ball
     * @param dx initial horizontal velocity
     * @param dy initial vertical velocity
     */
    public Ball(double cx, double cy, double r, double dx, double dy) {
        super(cx - r, cy - r, (int) r * 2, (int) r * 2, dx, dy);
        this.setSpeed(ORIGINAL_SPEED);
        this.setDirection(dx, dy);
        this.radius = r;
        this.renderer = new CircleRenderer(cx, cy, r, Color.RED);

        try {
            this.image = new Image(getClass().getResourceAsStream("/images/ball.png"));
            this.hitImage = new Image(getClass().getResourceAsStream("/images/ball_hit.png"));
        } catch (Exception e) {
            System.err.println("Failed to load images: " + e.getMessage());
            this.image = null;
        }
    }

    /**
     * Updates the ball's position, center coordinates, and hit effect timer.
     * Marks the ball as moving once it has non-zero velocity.
     *
     * @param deltaTime time elapsed since last frame in seconds
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (!(dx == 0 && dy == 0)) {
            moving = true;
        }

        this.cx = getPointX() + radius;
        this.cy = getPointY() + radius;

        if (isHit) {
            hitTimer -= deltaTime;
            if (hitTimer <= 0) {
                isHit = false;
            }
        }

        renderer.setPosition(cx, cy, this.width, this.height);
    }

    /**
     * Renders the ball using either loaded images or fallback circle.
     * Shows hit image briefly when {@code triggerHitEffect()} is called.
     *
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        if (image == null || hitImage == null) {
            renderer.render(gc);
        } else {
            if (isHit) {
                gc.drawImage(hitImage, getPointX(), getPointY(), getWidth(), getHeight());
            } else {
                gc.drawImage(image, getPointX(), getPointY(), getWidth(), getHeight());
            }
        }
    }

    /**
     * Reverses the horizontal velocity and triggers hit effect.
     * Used when colliding with left/right walls or paddle sides.
     */
    public void reverseX() {
        setDirection(-getDx(), getDy());
        triggerHitEffect();
    }

    /**
     * Reverses the vertical velocity and triggers hit effect.
     * Used when colliding with top wall, bricks, or paddle top.
     */
    public void reverseY() {
        setDirection(getDx(), -getDy());
        triggerHitEffect();
    }

    /**
     * Checks if the ball is currently in play.
     *
     * @return true if the ball is alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the alive status of the ball.
     *
     * @param alive whether the ball should be considered in play
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isMoving() {
        return moving;
    }

    public void stop() {
        this.moving = false;
        setDirection(0, 0);
    }

    /**
     * Resets the ball to a new position with default speed and stopped state.
     *
     * @param x new X coordinate (top-left)
     * @param y new Y coordinate (top-left)
     */
    public void reset(double x, double y) {
        setPointX(x);
        setPointY(y);
        setSpeed(ORIGINAL_SPEED);
        stop();
        alive = true;
        renderer.setPosition(this.getPointX() + radius, this.getPointY() + radius, this.width, this.height);
    }

    public double getRadius() {
        return radius;
    }

    public double getCx() {
        return cx;
    }

    public double getCy() {
        return cy;
    }

    /**
     * Triggers a brief visual hit effect using the hit image.
     * Only activates if hit image is loaded and not already active.
     */
    public void triggerHitEffect() {
        if (hitImage != null && !isHit) {
            isHit = true;
            hitTimer = HIT_DURATION;
        }
    }
}
