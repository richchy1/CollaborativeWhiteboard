package model;

import java.awt.*;

public class EraseAction extends DrawingAction {
    private final int x1, y1, x2, y2;
    private final int thickness;

    public EraseAction(int x1, int y1, int x2, int y2, int thickness) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.thickness = thickness;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE); // Erases by drawing over with background color
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(x1, y1, x2, y2);
    }
}