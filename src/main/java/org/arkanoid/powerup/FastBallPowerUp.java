package org.arkanoid.powerup;

import org.arkanoid.core.App;
import org.arkanoid.entity.Ball;
import org.arkanoid.entity.MovableObject;

public class FastBallPowerUp extends TimedPowerUp {
    private double multiplier;
    private double originalSpeed;

    public FastBallPowerUp(double multiplier, double duration) {
        super("FastBall", duration);
        this.multiplier = multiplier;
        this.originalSpeed = 0;
    }

    @Override
    public void applyEffect(MovableObject ball) {
        if (ball instanceof Ball) {
            Ball b =  (Ball) ball;
            originalSpeed = b.getSpeed();
            b.setSpeed(b.getSpeed() * multiplier);
        }
    }

    @Override
    public void removeEffect(MovableObject ball) {
        if (ball instanceof Ball) {
            Ball b =  (Ball) ball;
            b.setSpeed(originalSpeed);
        }
    }
}