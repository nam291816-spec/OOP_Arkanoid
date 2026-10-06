package org.arkanoid.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleRenderer extends Renderer {
    public RectangleRenderer(double x, double y, double width, double height, Color color) {
        super(x, y, width, height, color);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }
}
