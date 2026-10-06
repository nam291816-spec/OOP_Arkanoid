package org.arkanoid.powerup;

import org.arkanoid.core.App;
import org.arkanoid.entity.MovableObject;
import org.arkanoid.entity.Paddle;

public class Boom extends PermanentPowerUp {
    public Boom() {

    }

    @Override
    public void applyEffect(MovableObject movableObject, App app) {
        if (movableObject instanceof Paddle) {
            app.addLives(-1);
//            app.addScore(-100);
        }
    }
}
