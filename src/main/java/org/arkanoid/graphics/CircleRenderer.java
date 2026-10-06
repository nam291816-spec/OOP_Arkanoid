package org.arkanoid.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircleRenderer extends Renderer{
    private double radius;

    public CircleRenderer(double cx, double cy, double radius, Color color){
        super(cx - radius, cy - radius, radius * 2, radius * 2, color);
        this.radius = radius;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public void setPosition(double cx, double cy, int width, int height) {
        this.x = cx;
        this.y = cy;
    }
}