package gui;

import javax.swing.*;
import java.awt.*;

public class WhiteboardGUI extends JFrame {
    public WhiteboardGUI(){
        setTitle("Collaborative Whiteboard");
        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();
        CanvasPanel canvasPanel = new CanvasPanel();
        JPanel userPanel = new JPanel();
        JLabel userLabel = new JLabel("Users");
        JLabel statusLabel = new JLabel("Not connected");
        JTextArea userListArea = new JTextArea(0, 15);
        userListArea.setEditable(false);

        userPanel.add(userLabel);
        userPanel.add(userListArea);



        JButton connectBtn = new JButton("Connect");
        JButton penBtn     = new JButton("Pen");
        JButton eraserBtn  = new JButton("Eraser");
        JButton textBtn    = new JButton("Text");
        JButton colorBtn   = new JButton("Color");
        JButton undoBtn    = new JButton("Undo");
        JButton clearBtn   = new JButton("Clear");
        JButton saveBtn    = new JButton("Save");
        JButton loadBtn    = new JButton("Load");

        toolbar.add(connectBtn);
        toolbar.add(penBtn);
        toolbar.add(eraserBtn);
        toolbar.add(textBtn);
        toolbar.add(colorBtn);
        toolbar.add(undoBtn);
        toolbar.add(clearBtn);
        toolbar.add(saveBtn);
        toolbar.add(loadBtn);

        add(toolbar, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);
        add(userPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);


    }
}
