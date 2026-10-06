package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.arkanoid.graphics.RectangleRenderer;
import org.arkanoid.powerup.*;

import static javafx.scene.paint.Color.WHITE;

public class Item extends MovableObject{
    private PowerUp item;

    private final RectangleRenderer renderer;
    private Image image;

    public Item(double x,double y, int width, int height, PowerUp item) {
        super(x, y, width, height);
        this.item = item;
        this.setSpeed(200);
        this.setDirection(0, 1);
        renderer = new RectangleRenderer(x, y, width, height, WHITE);

        if (item instanceof ExpandPaddlePowerUp) {
            image = new Image(getClass().getResourceAsStream("/images/expand_paddle.png"));
        } else if (item instanceof FastBallPowerUp) {
            image = new Image(getClass().getResourceAsStream("/images/fast_ball.png"));
        } else if (item instanceof ExtraLife) {
            image = new Image(getClass().getResourceAsStream("/images/heart.png"));
        } else if (item instanceof Boom) {
            image = new Image(getClass().getResourceAsStream("/images/boom.png"));
        } else {
            image = null;
        }
    }

    public PowerUp getPowerUp() {
        return item;
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        renderer.setPosition(this.getPointX() + 10, this.getPointY() + 10, this.width, this.height);
    }
    @Override
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, getPointX(), getPointY(), getWidth(), getHeight());
        } else {
            renderer.render(gc);
        }
    }
}
