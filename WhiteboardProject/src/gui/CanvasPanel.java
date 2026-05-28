package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CanvasPanel extends JPanel {
    int lastX, lastY;
    boolean drawing = false;
    public CanvasPanel(){
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
                lastX = e.getX();
                lastY = e.getY();
                drawing = true;
            }

            public void mouseReleased(MouseEvent e){
                drawing = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e){
                if(drawing){
                    Graphics obj = getGraphics();
                    obj.setColor(Color.BLACK);
                    obj.drawLine(lastX, lastY, e.getX(), e.getY());
                    lastX = e.getX();
                    lastY = e.getY();

                }
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

}
