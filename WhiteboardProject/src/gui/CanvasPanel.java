package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.Tool;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import model.DrawingAction;
import model.DrawLineAction;
import model.EraseAction;
import model.TextAction;
import java.util.ArrayList;
import java.util.List;

public class CanvasPanel extends JPanel {
    List<DrawingAction> history = new ArrayList<>();
    int lastX, lastY;
    boolean drawing = false;
    Tool currentTool = Tool.PEN;
    Color currentColor = Color.BLACK;
    public CanvasPanel(){
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e){
                lastX = e.getX();
                lastY = e.getY();
                drawing = true;
                if(currentTool == Tool.TEXT){
                    String text = JOptionPane.showInputDialog("Enter text: ");
                    if(text!=null && !text.isBlank()){
                        DrawingAction txtact = new TextAction(e.getX(), e.getY(), text, currentColor, 16);
                        history.add(txtact);
                        repaint();
                    }
                    drawing = false;
                }
            }

            public void mouseReleased(MouseEvent e){
                drawing = false;
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e){
                if(drawing){
                    if(currentTool == Tool.PEN){
                        DrawingAction act = new DrawLineAction(lastX, lastY, e.getX(), e.getY(), currentColor, 2);
                        history.add(act);
                    }else if(currentTool == Tool.ERASER){
                        DrawingAction act = new EraseAction(lastX,lastY,e.getX(),e.getY(),15);
                        history.add(act);
                    }
                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
                }
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(canvas,0,0,null);
    }

    public void setTool(Tool t){
        currentTool = t;
    }

    public void setColor(Color c){
        currentColor = c;
    }

}
