package org.arkanoid.powerup;

import org.arkanoid.core.App;
import org.arkanoid.entity.MovableObject;

public abstract class PermanentPowerUp extends PowerUp {
    public PermanentPowerUp() {

    }

    public abstract void applyEffect(MovableObject movableObject, App app);
}
