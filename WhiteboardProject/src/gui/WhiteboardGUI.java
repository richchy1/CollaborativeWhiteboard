package gui;

import client.WhiteboardClient;
import model.DrawingAction;
import model.Message;
import model.MessageType;
import model.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WhiteboardGUI extends JFrame {
    private final CanvasPanel canvasPanel;
    private final JTextArea userListArea;
    private final JLabel statusLabel;

    private WhiteboardClient client;

    public WhiteboardGUI() {
        setTitle("Collaborative Whiteboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();

        canvasPanel = new CanvasPanel();

        JPanel userPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel("Users");
        userListArea = new JTextArea(0, 15);
        userListArea.setEditable(false);

        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userListArea), BorderLayout.CENTER);

        statusLabel = new JLabel("Not connected");

        JButton connectBtn = new JButton("Connect");
        JButton penBtn = new JButton("Pen");
        JButton eraserBtn = new JButton("Eraser");
        JButton textBtn = new JButton("Text");
        JButton colorBtn = new JButton("Color");
        JButton undoBtn = new JButton("Undo");
        JButton clearBtn = new JButton("Clear");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");

        connectBtn.addActionListener(e -> connectToServer());

        penBtn.addActionListener(e -> canvasPanel.setTool(Tool.PEN));
        eraserBtn.addActionListener(e -> canvasPanel.setTool(Tool.ERASER));
        textBtn.addActionListener(e -> canvasPanel.setTool(Tool.TEXT));

        colorBtn.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Choose color", Color.BLACK);

            if (color != null) {
                canvasPanel.setColor(color);
            }
        });

        clearBtn.addActionListener(e -> {
            if (client != null && client.isConnected()) {
                client.sendMessage(new Message(MessageType.CLEAR_REQUEST));
            } else {
                canvasPanel.clearCanvas();
            }
        });

        undoBtn.addActionListener(e -> {
            if (client != null && client.isConnected()) {
                client.sendMessage(new Message(MessageType.UNDO_REQUEST));
            } else {
                canvasPanel.undoLastAction();
            }
        });

        saveBtn.addActionListener(e -> {
            if (client != null && client.isConnected()) {
                client.sendMessage(new Message(MessageType.SAVE_REQUEST));
            } else {
                showStatus("Save works through server in multiplayer mode.");
            }
        });

        loadBtn.addActionListener(e -> {
            if (client != null && client.isConnected()) {
                client.sendMessage(new Message(MessageType.LOAD_REQUEST));
            } else {
                showStatus("Load works through server in multiplayer mode.");
            }
        });

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

    private void connectToServer() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");

        if (username == null || username.isBlank()) {
            showStatus("Username cannot be empty.");
            return;
        }

        String host = JOptionPane.showInputDialog(this, "Enter server IP:", "localhost");

        if (host == null || host.isBlank()) {
            showStatus("Server IP cannot be empty.");
            return;
        }

        client = new WhiteboardClient(this);
        canvasPanel.setClient(client);

        boolean connected = client.connect(host, 5000, username);

        if (connected) {
            showStatus("Connected as " + username);
        } else {
            showStatus("Connection failed.");
        }
    }

    public void addAction(DrawingAction action) {
        canvasPanel.addAction(action);
    }

    public void setHistory(List<DrawingAction> history) {
        canvasPanel.setHistory(history);
    }

    public void updateUserList(List<String> users) {
        userListArea.setText("");

        if (users == null) {
            return;
        }

        for (String user : users) {
            userListArea.append(user + "\n");
        }
    }

    public void showStatus(String message) {
        statusLabel.setText(message);
    }
}