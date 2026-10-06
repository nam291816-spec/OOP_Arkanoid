package org.arkanoid.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.arkanoid.graphics.RectangleRenderer;

public class UnbreakableBrick extends Brick{

    private final RectangleRenderer renderer;
    private Image image;

    public UnbreakableBrick(double x, double y, int width, int height, int hitPoints, String id) {
        super(x, y, width, height, Integer.MAX_VALUE, id);

        this.renderer = new RectangleRenderer((int) x ,(int) y, width, height, Color.YELLOW);
        try {
            this.image = new Image(getClass().getResourceAsStream("/images/unbreakable_brick.png"));
        } catch (Exception e) {
            System.err.println("Failed to load unbreakable_brick.png: " + e.getMessage());
            this.image = null;
        }
    }

    @Override
    public void update(double deltaTime) {

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
