package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.Tool;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

public class CanvasPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D obj2d;
    int lastX, lastY;
    boolean drawing = false;
    Tool currentTool = Tool.PEN;
    Color currentColor = Color.BLACK;
    public CanvasPanel(){
        setBackground(Color.WHITE);

        canvas = new BufferedImage(1200,800,BufferedImage.TYPE_INT_ARGB);
        obj2d = (Graphics2D) canvas.getGraphics();
        obj2d.setColor(Color.WHITE);
        obj2d.fillRect(0,0,1200,800);

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e){
                lastX = e.getX();
                lastY = e.getY();
                drawing = true;
                if(currentTool == Tool.TEXT){
                    String text = JOptionPane.showInputDialog("Enter text: ");
                    if(text!=null && !text.isBlank()){
                        obj2d.setColor(currentColor);
                        obj2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);//to make the text look cleaner
                        obj2d.setFont(new Font("SansSerif", Font.PLAIN, 16));
                        obj2d.drawString(text, e.getX(),e.getY());
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
