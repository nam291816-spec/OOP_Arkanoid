package org.arkanoid.powerup;

import org.arkanoid.entity.MovableObject;

public abstract class TimedPowerUp extends PowerUp {
    protected double duration;
    protected double timer;
    protected boolean active;
    protected boolean collected;

    public TimedPowerUp() {
        this.active = false;
        this.collected = false;
        this.duration = 5.0;
        this.timer = 0;
    }

    public TimedPowerUp(String type, double duration) {
        this.type = type;
        this.duration = duration;
        this.active = false;
        this.collected = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void update(double deltaTime, MovableObject target) {
        if (active) {
            timer += deltaTime;
            if (timer >= duration) {
                deactivate(target);
            }
        }
    }

    public void activate(MovableObject target) {
        if (!active && !collected) {
            setActive(true);
            setCollected(true);
            timer = 0;
            applyEffect(target);
        }
    }

    public void deactivate(MovableObject target) {
        if (active) {
            setActive(false);
            setCollected(false);
            timer = 0;
            removeEffect(target);
        }
    }

    public abstract void applyEffect(MovableObject movableObject);
    public abstract void removeEffect(MovableObject movableObject);
}
