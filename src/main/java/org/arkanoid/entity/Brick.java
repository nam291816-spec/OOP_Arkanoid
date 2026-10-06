package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all brick types in the Arkanoid game.
 * Extends GameObject to inherit position and dimensions.
 * Manages hit points (durability), unique ID, and destruction state.
 * Subclasses must implement update and render methods.
 */
public abstract class Brick extends GameObject{

    private int hitPoints;
    private String id;

    public static final String NB = "NormalBrick";
    public static final String SB = "StrongBrick";
    public static final String UB = "UnbreakableBrick";

    public Brick(double x, double y, int width, int height, int hitPoints, String id) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.id = id;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public String getId() {
        return id;
    }

    public void hit() {
        if (hitPoints > 0) {
            hitPoints--;
        }
    }

    public boolean isDestroyed() {
        return hitPoints == 0;
    }



    @Override
    public abstract void update(double deltaTime);

    @Override
    public abstract void render(GraphicsContext gc);

}
