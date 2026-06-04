package gui;

import client.WhiteboardClient;
import model.DrawingAction;
import model.DrawLineAction;
import model.EraseAction;
import model.TextAction;
import model.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CanvasPanel extends JPanel {
    private List<DrawingAction> history = new ArrayList<>();

    private int lastX;
    private int lastY;
    private boolean drawing = false;

    private Tool currentTool = Tool.PEN;
    private Color currentColor = Color.BLACK;

    private WhiteboardClient client;

    public CanvasPanel() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                drawing = true;

                if (currentTool == Tool.TEXT) {
                    String text = JOptionPane.showInputDialog("Enter text:");

                    if (text != null && !text.isBlank()) {
                        if (text.length() > 100) {
                            text = text.substring(0, 100);
                        }

                        DrawingAction action =
                                new TextAction(e.getX(), e.getY(), text, currentColor, 16);

                        sendOrAddAction(action);
                    }

                    drawing = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!drawing) {
                    return;
                }

                DrawingAction action = null;

                if (currentTool == Tool.PEN) {
                    action = new DrawLineAction(
                            lastX, lastY,
                            e.getX(), e.getY(),
                            currentColor,
                            2
                    );
                } else if (currentTool == Tool.ERASER) {
                    action = new EraseAction(
                            lastX, lastY,
                            e.getX(), e.getY(),
                            15
                    );
                }

                if (action != null) {
                    sendOrAddAction(action);
                }

                lastX = e.getX();
                lastY = e.getY();
            }
        });
    }

    private void sendOrAddAction(DrawingAction action) {
        if (client != null && client.isConnected()) {
            client.sendAction(action);
        } else {
            addAction(action);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage canvas =
                new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = canvas.createGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (DrawingAction action : history) {
            action.draw(g2);
        }

        g2.dispose();

        g.drawImage(canvas, 0, 0, null);
    }

    public void addAction(DrawingAction action) {
        history.add(action);
        repaint();
    }

    public void setHistory(List<DrawingAction> history) {
        if (history == null) {
            this.history = new ArrayList<>();
        } else {
            this.history = new ArrayList<>(history);
        }

        repaint();
    }

    public List<DrawingAction> getHistory() {
        return new ArrayList<>(history);
    }

    public void clearCanvas() {
        history.clear();
        repaint();
    }

    public void undoLastAction() {
        if (!history.isEmpty()) {
            history.remove(history.size() - 1);
            repaint();
        }
    }

    public void setTool(Tool tool) {
        currentTool = tool;
    }

    public void setColor(Color color) {
        if (color != null) {
            currentColor = color;
        }
    }

    public void setClient(WhiteboardClient client) {
        this.client = client;
    }
}