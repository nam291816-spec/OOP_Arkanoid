package org.arkanoid.core;

import org.arkanoid.entity.*;
import org.arkanoid.powerup.*;

import java.util.List;

/**
 * The CollisionManager class handles collision detection and resolution between game objects
 * in the Arkanoid game, such as the ball, paddle, bricks, walls, and items (power-ups).
 * It uses Axis-Aligned Bounding Box (AABB) collision detection and manages the game logic
 * for interactions like ball bounces, brick destruction, and power-up activation.
 */
public class CollisionManager {

    /**
     * Checks for collision between two GameObjects using Axis-Aligned Bounding Box (AABB) method.
     * This method determines if the bounding boxes of the two objects overlap.
     *
     * @param a The first GameObject.
     * @param b The second GameObject.
     * @return true if the objects collide, false otherwise.
     */
    public static boolean isColliding(GameObject a, GameObject b) {
        return a.getPointX() <= b.getPointX() + b.getWidth() &&
                a.getPointX() + a.getWidth() >= b.getPointX() &&
                a.getPointY() <= b.getPointY() + b.getHeight() &&
                a.getPointY() + a.getHeight() >= b.getPointY();
    }

    /**
     * Handles collision between the ball and the paddle.
     * Determines whether the ball hits the top or a side of the paddle and
     * delegates to the appropriate handler.
     *
     * @param ball   the Ball object
     * @param paddle the Paddle object
     */
    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!isColliding(ball, paddle)) return;

        // Calculate edge coordinates
        double ballLeft   = ball.getPointX();
        double ballRight  = ball.getPointX() + ball.getWidth();
        double ballTop    = ball.getPointY();
        double ballBottom = ball.getPointY() + ball.getHeight();

        double paddleLeft   = paddle.getPointX();
        double paddleRight  = paddle.getPointX() + paddle.getWidth();
        double paddleTop    = paddle.getPointY();
        double paddleBottom = paddle.getPointY() + paddle.getHeight();

        // Overlap on each side
        double overlapLeft   = ballRight - paddleLeft;
        double overlapRight  = paddleRight - ballLeft;
        double overlapTop    = ballBottom - paddleTop;
        double overlapBottom = paddleBottom - ballTop;

        // No real overlap – exit
        if (overlapLeft <= 0 || overlapRight <= 0 || overlapTop <= 0 || overlapBottom <= 0) {
            return;
        }

        // Find the smallest overlap to determine collision side
        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (minOverlap == overlapLeft || minOverlap == overlapRight) {
            handleSideCollision(ball, paddle, minOverlap == overlapLeft);
        } else {
            if (minOverlap == overlapTop) {
                handleTopCollision(ball, paddle);
            }
        }
    }

    /**
     * Handles collision when the ball hits the TOP surface of the paddle.
     * Computes a bounce angle based on the hit position:
     *   - centre → straight up
     *   - edges → diagonal (max ±60°)
     *
     * @param ball   the Ball object
     * @param paddle the Paddle object
     */
    private static void handleTopCollision(Ball ball, Paddle paddle) {
        // Push ball out of the paddle on the Y-axis
        ball.setPointY(paddle.getPointY() - ball.getHeight());

        // Relative hit position (-1.0 = left edge, 0 = centre, +1.0 = right edge)
        double paddleCenterX = paddle.getPointX() + paddle.getWidth() / 2.0;
        double hitOffset = (ball.getCx() - paddleCenterX) / (paddle.getWidth() / 2.0);
        hitOffset = Math.max(-1.0, Math.min(1.0, hitOffset)); // clamp

        // Maximum bounce angle = 60°
        double maxAngle = Math.PI / 3;
        double angle = hitOffset * maxAngle;

        // New direction vector (speed preserved)
        double newDx = Math.sin(angle);
        double newDy = -Math.cos(angle); // always bounce upward

        ball.setDirection(newDx, newDy);
        ball.triggerHitEffect();
    }

    /**
     * Handles collision when the ball hits the LEFT or RIGHT side of the paddle.
     *   - Paddle stationary → simple X-reverse
     *   - Paddle moving → ball is pushed opposite to paddle direction
     *                     and also lifted upward to avoid sliding.
     *
     * @param ball        the Ball object
     * @param paddle      the Paddle object
     * @param hitLeftSide true if the ball hit the left side
     */
    private static void handleSideCollision(Ball ball, Paddle paddle, boolean hitLeftSide) {
        // Push ball out of the paddle to prevent sticking
        if (hitLeftSide) {
            ball.setPointX(paddle.getPointX() - ball.getWidth() - 0.1);
        } else {
            ball.setPointX(paddle.getPointX() + paddle.getWidth() + 0.1);
        }

        double padDx  = paddle.getDx();
        double ballDx = ball.getDx();

        // Paddle not moving → wall-like bounce
        if (padDx == 0) {
            ball.reverseX();
            ball.triggerHitEffect();
            return;
        }

        // Paddle moving → reverse ball horizontally opposite to paddle motion
        if (padDx > 0) {
            ball.setDx(-Math.abs(ballDx)); // paddle right → ball left
        } else {
            ball.setDx(Math.abs(ballDx));  // paddle left → ball right
        }

        // Lift the ball upward to avoid sliding along the side
        ball.setDy(-Math.abs(ball.getDy()));

        // Add a small spin based on paddle speed
        ball.setDx(ball.getDx() + padDx * 0.15);

        ball.triggerHitEffect();
    }



    /**
     * Handles collision between the ball and bricks.
     * Determines the collision direction based on the ball's velocity and adjusts the ball's
     * position to prevent sticking or passing through bricks. Only affects non-unbreakable bricks.
     *
     * @param ball   The Ball object.
     * @param bricks List of Brick objects to check for collision.
     */
    public static void handleBallBricksCollision(Ball ball, List<Brick> bricks) {
        Brick collidedBrick = null;
        double minDistance = Double.MAX_VALUE;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (isColliding(ball, brick)) {
                double brickCenterX = brick.getPointX() + brick.getWidth() / 2.0;
                double brickCenterY = brick.getPointY() + brick.getHeight() / 2.0;
                double distance = Math.sqrt(Math.pow(ball.getCx() - brickCenterX, 2) + Math.pow(ball.getCy() - brickCenterY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    collidedBrick = brick;
                }
            }
        }

        if (collidedBrick != null) {
            if (!(collidedBrick instanceof UnbreakableBrick)) {
                collidedBrick.hit();
            }

            double ballLeft = ball.getPointX();
            double ballRight = ball.getPointX() + ball.getWidth();
            double ballTop = ball.getPointY();
            double ballBottom = ball.getPointY() + ball.getHeight();

            double brickLeft = collidedBrick.getPointX();
            double brickRight = collidedBrick.getPointX() + collidedBrick.getWidth();
            double brickTop = collidedBrick.getPointY();
            double brickBottom = collidedBrick.getPointY() + collidedBrick.getHeight();

            double overlapLeft = ballRight - brickLeft;
            double overlapRight = brickRight - ballLeft;
            double overlapTop = ballBottom - brickTop;
            double overlapBottom = brickBottom - ballTop;

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                ball.reverseX();
                if (minOverlap == overlapLeft) {
                    ball.setPointX(brickLeft - ball.getWidth());
                } else {
                    ball.setPointX(brickRight);
                }
            } else {
                ball.reverseY();
                if (minOverlap == overlapTop) {
                    ball.setPointY(brickTop - ball.getHeight());
                } else {
                    ball.setPointY(brickBottom);
                }
            }
        }
    }

    /**
     * Handles collision between the ball and the game walls (left, right, and top boundaries).
     * Resets the ball if it hits the bottom boundary (out of bounds).
     *
     * @param ball The Ball object.
     */
    public static void handleBallWallCollision(Ball ball) {
        if (ball.getPointX() <= 0 || ball.getPointX() + ball.getWidth() >= App.WIDTH) {
            ball.reverseX();
            if (ball.getPointX() <= 0) {
                ball.setPointX(0);
            } else {
                ball.setPointX(App.WIDTH - ball.getWidth());
            }
        }
        if (ball.getPointY() < 50) {
            ball.reverseY();
            ball.setPointY(50);
        } else if (ball.getPointY() + ball.getHeight() >= App.HEIGHT) {
            ball.setAlive(false);
            ball.stop();
        }
    }

    /**
     * Handles collision between the paddle and an item (power-up).
     * Activates the power-up effect if a collision occurs and adds it to the active power-ups list.
     *
     * @param item   The Item object (power-up).
     * @param paddle The Paddle object.
     * @param app    The App instance managing the game state.
     */
    public static void handlePaddleItemCollision(Item item, Paddle paddle, App app) {
        if (!isColliding(paddle, item)) {
            return;
        }

        PowerUp powerUp = item.getPowerUp();
        if (powerUp instanceof TimedPowerUp) {
            TimedPowerUp timedPowerUp = (TimedPowerUp) powerUp;
            if (timedPowerUp instanceof ExpandPaddlePowerUp) {
                timedPowerUp.activate(paddle);
                app.addActivePowerUp(timedPowerUp, paddle);
            } else if (timedPowerUp instanceof FastBallPowerUp) {
                timedPowerUp.activate(app.getBall());
                app.addActivePowerUp(timedPowerUp, app.getBall());
            }
        }
        else if (powerUp instanceof PermanentPowerUp) {
            PermanentPowerUp permanentPowerUp = (PermanentPowerUp) powerUp;
            if (permanentPowerUp instanceof ExtraLife) {
                permanentPowerUp.applyEffect(paddle, app);
            } else if (permanentPowerUp instanceof Boom) {
                permanentPowerUp.applyEffect(paddle, app);
            }
        }
    }
}