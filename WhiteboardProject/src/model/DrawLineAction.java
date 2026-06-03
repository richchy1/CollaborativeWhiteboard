package model;

import java.awt.*;

public class DrawLineAction extends DrawingAction {
    private final int x1, y1, x2, y2;
    private final Color color;
    private final int thickness;

    public DrawLineAction(int x1, int y1, int x2, int y2, Color color, int thickness) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(x1, y1, x2, y2);
    }
}