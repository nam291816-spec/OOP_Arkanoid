package org.arkanoid.powerup;

import org.arkanoid.entity.MovableObject;
import org.arkanoid.entity.Paddle;

public class ExpandPaddlePowerUp extends TimedPowerUp {
    private double scale;
    private int originalWidth;

    public ExpandPaddlePowerUp(double scale,double duration) {
        super("Expandpaddle", duration);
        this.scale = scale;
        this.originalWidth = 0;
    }

    @Override
    public void applyEffect(MovableObject paddle) {
        if (paddle instanceof Paddle) {
            Paddle p = (Paddle) paddle;
            originalWidth = p.getWidth();
            p.setWidth((int) (originalWidth * scale));
        }
    }

    @Override
    public void removeEffect(MovableObject paddle) {
        if (paddle instanceof Paddle) {
            Paddle p = (Paddle) paddle;
            p.setWidth(Paddle.ORIGINAL_WIDTH);
        }
    }
}