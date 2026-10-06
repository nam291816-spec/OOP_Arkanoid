package org.arkanoid.powerup;

public class PowerUp {
    protected String type;

    public PowerUp() {
    }

    public PowerUp(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}