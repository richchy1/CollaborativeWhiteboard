package model;

import java.awt.Graphics2D;
import java.io.Serializable;

public abstract class DrawingAction implements Serializable {
    public abstract void draw(Graphics2D obj);
}
