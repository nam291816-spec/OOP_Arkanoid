package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class StrongBrick extends Brick {

    private final Image[] images;

    public StrongBrick(double x, double y, int width, int height, int hitPoints, String id) {
        super(x, y, width, height, 4, id);

        images = new Image[4];
        images[3] = new Image(getClass().getResourceAsStream("/images/strong_brick_4.png"));
        images[2] = new Image(getClass().getResourceAsStream("/images/strong_brick_3.png"));
        images[1] = new Image(getClass().getResourceAsStream("/images/strong_brick_2.png"));
        images[0] = new Image(getClass().getResourceAsStream("/images/strong_brick_1.png"));
    }


    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            int hp = getHitPoints();

            Image img = images[hp - 1];
            gc.drawImage(img, getPointX(), getPointY(), getWidth(), getHeight());
        }
    }
}