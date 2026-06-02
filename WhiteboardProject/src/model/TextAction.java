package model;

import java.awt.*;

public class TextAction extends DrawingAction {
    private final int x, y;
    private final String text;
    private final Color color;
    private final int fontSize;

    public TextAction(int x, int y, String text, Color color, int fontSize) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.fontSize = fontSize;
    }

    @Override
    public void draw(Graphics2D g) {
        if (text != null && !text.isBlank()) {
            g.setColor(color);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            g.drawString(text, x, y);
        }
    }
}