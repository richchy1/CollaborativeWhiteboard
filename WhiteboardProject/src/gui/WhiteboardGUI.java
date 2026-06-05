package gui;

import client.WhiteboardClient;
import model.DrawingAction;
import model.Message;
import model.MessageType;
import model.Tool;
import model.WhiteboardFileManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardGUI extends JFrame {

    private final CanvasPanel canvasPanel;
    private final JPanel userListPanel;
    private final JLabel statusLabel;
    private final JLabel toolLabel;

    private JButton connectBtn, disconnectBtn, penBtn, eraserBtn, textBtn;
    private JButton colorBtn, undoBtn, clearBtn, saveBtn, loadBtn;
    private final List<JButton> brushButtons = new ArrayList<>();

    private Color currentColor = Color.BLACK;
    private WhiteboardClient client;

    private static final Color TOOLBAR_BG   = new Color(30, 30, 46);
    private static final Color TOOLBAR_TEXT = new Color(205, 214, 244);
    private static final Color ACTIVE_COLOR = new Color(127, 119, 221);
    private static final Color GREEN_COLOR  = new Color(0, 250, 97);
    private static final Color STATUS_BG    = new Color(30, 30, 46);

    public WhiteboardGUI() {
        setTitle("Collaborative Whiteboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(TOOLBAR_BG);
        toolbar.setBorder(new EmptyBorder(6, 10, 6, 10));

        penBtn      = makeToolbarBtn("Pen",      "ti ti-pencil");
        eraserBtn   = makeToolbarBtn("Eraser",   "ti ti-eraser");
        textBtn     = makeToolbarBtn("Text",     "ti ti-text-size");
        colorBtn    = makeColorBtn();
        undoBtn     = makeToolbarBtn("Undo",     "ti ti-arrow-back-up");
        clearBtn    = makeToolbarBtn("Clear",    "ti ti-trash");
        saveBtn     = makeToolbarBtn("Save",     "ti ti-device-floppy");
        loadBtn     = makeToolbarBtn("Load",     "ti ti-folder-open");
        connectBtn  = makeConnectBtn("Connect",  GREEN_COLOR);
        disconnectBtn = makeConnectBtn("Disconnect", new Color(255, 0, 0));

        penBtn.setEnabled(false);
        eraserBtn.setEnabled(false);
        textBtn.setEnabled(false);
        colorBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        loadBtn.setEnabled(false);
        disconnectBtn.setEnabled(false);

        toolbar.add(penBtn);
        toolbar.add(eraserBtn);
        toolbar.add(textBtn);
        toolbar.add(colorBtn);
        toolbar.addSeparator();
        toolbar.add(undoBtn);
        toolbar.add(clearBtn);
        toolbar.addSeparator();
        toolbar.add(saveBtn);
        toolbar.add(loadBtn);

        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(connectBtn);
        toolbar.add(Box.createHorizontalStrut(6));
        toolbar.add(disconnectBtn);

        canvasPanel = new CanvasPanel();

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setBackground(UIManager.getColor("Panel.background"));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
                new Color(200, 200, 200, 60)));

        JLabel usersHeader = new JLabel("  Online");
        usersHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        usersHeader.setForeground(new Color(120, 120, 140));
        usersHeader.setBorder(new EmptyBorder(10, 8, 8, 8));
        usersHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200, 60)),
                new EmptyBorder(8, 10, 8, 8)
        ));

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setOpaque(false);
        userListPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane userScroll = new JScrollPane(userListPanel);
        userScroll.setBorder(null);
        userScroll.setOpaque(false);
        userScroll.getViewport().setOpaque(false);

        JPanel brushPanel = new JPanel();
        brushPanel.setLayout(new BoxLayout(brushPanel, BoxLayout.Y_AXIS));
        brushPanel.setOpaque(false);
        brushPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200, 60)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel brushLabel = new JLabel("Brush size");
        brushLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        brushLabel.setForeground(new Color(120, 120, 140));
        brushLabel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel brushBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        brushBtns.setOpaque(false);
        brushBtns.setAlignmentX(LEFT_ALIGNMENT);

        int[] sizes = {2, 5, 10};
        String[] labels = {"S", "M", "L"};

        for (int i = 0; i < sizes.length; i++) {
            final int size = sizes[i];
            JButton b = new JButton(labels[i]);
            b.setFont(new Font("SansSerif", Font.BOLD, 11));
            b.setPreferredSize(new Dimension(36, 30));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            brushButtons.add(b);
            if (i == 0) {
                b.setBackground(new Color(238, 237, 254));
                b.setForeground(ACTIVE_COLOR);
                b.setBorder(BorderFactory.createLineBorder(ACTIVE_COLOR, 1));
            } else {
                b.setBackground(UIManager.getColor("Button.background"));
                b.setForeground(UIManager.getColor("Button.foreground"));
                b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            }
            b.addActionListener(e -> {
                canvasPanel.setThickness(size);
                setSelectedBrushButton(b);
            });
            brushBtns.add(b);
        }

        brushPanel.add(brushLabel);
        brushPanel.add(brushBtns);

        sidebar.add(usersHeader, BorderLayout.NORTH);
        sidebar.add(userScroll, BorderLayout.CENTER);
        sidebar.add(brushPanel, BorderLayout.SOUTH);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(STATUS_BG);
        statusBar.setBorder(new EmptyBorder(5, 14, 5, 14));

        statusLabel = new JLabel("● Not connected");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(166, 173, 200));

        toolLabel = new JLabel("No tool selected");
        toolLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        toolLabel.setForeground(ACTIVE_COLOR);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(toolLabel, BorderLayout.EAST);

        add(toolbar, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        penBtn.addActionListener(e -> {
            canvasPanel.setTool(Tool.PEN);
            setActiveTool(penBtn, "✏ Pen tool active");
        });
        eraserBtn.addActionListener(e -> {
            canvasPanel.setTool(Tool.ERASER);
            setActiveTool(eraserBtn, "◻ Eraser tool active");
        });
        textBtn.addActionListener(e -> {
            canvasPanel.setTool(Tool.TEXT);
            setActiveTool(textBtn, "T Text tool active");
        });
        colorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose color", currentColor);
            if (chosen != null) {
                currentColor = chosen;
                canvasPanel.setColor(chosen);
                updateColorDot(chosen);
            }
        });
        undoBtn.addActionListener(e -> {
            if (client != null && client.isConnected()) {
                client.sendMessage(new Message(MessageType.UNDO_REQUEST));
            } else {
                canvasPanel.undoLastAction();
            }
        });
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Clear the entire canvas?", "Confirm Clear",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (client != null && client.isConnected()) {
                    client.sendMessage(new Message(MessageType.CLEAR_REQUEST));
                } else {
                    canvasPanel.clearCanvas();
                }
            }
        });
        saveBtn.addActionListener(e -> handleSave());
        loadBtn.addActionListener(e -> handleLoad());
        connectBtn.addActionListener(e -> connectToServer());
        disconnectBtn.addActionListener(e -> disconnectFromServer());

        setVisible(true);
    }

    private JButton makeToolbarBtn(String text, String iconClass) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(TOOLBAR_TEXT);
        btn.setBackground(TOOLBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(50, 50, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(ACTIVE_COLOR))
                    btn.setBackground(TOOLBAR_BG);
            }
        });
        return btn;
    }

    private JButton makeColorBtn() {
        JButton btn = new JButton("● Color");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(TOOLBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        return btn;
    }

    private JButton makeConnectBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(5, 14, 5, 14)
        ));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton activeToolBtn = null;

    private void setActiveTool(JButton btn, String toolText) {
        if (activeToolBtn != null) {
            activeToolBtn.setBackground(TOOLBAR_BG);
            activeToolBtn.setForeground(TOOLBAR_TEXT);
        }
        btn.setBackground(ACTIVE_COLOR);
        btn.setForeground(Color.WHITE);
        activeToolBtn = btn;
        toolLabel.setText(toolText);
    }

    private void updateColorDot(Color color) {
        colorBtn.setForeground(color);
        colorBtn.setText("● Color");
    }

    private void addUserCard(String username) {
        String initials = username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : username.toUpperCase();

        Color[] avatarColors = {
                new Color(238, 237, 254), new Color(225, 245, 238),
                new Color(250, 236, 231), new Color(230, 241, 251)
        };
        Color[] textColors = {
                new Color(60, 52, 137), new Color(8, 80, 65),
                new Color(113, 43, 19), new Color(12, 68, 124)
        };
        int idx = Math.abs(username.hashCode()) % avatarColors.length;

        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 60), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        card.setBackground(UIManager.getColor("Panel.background"));
        card.setOpaque(true);

        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 11));
        avatar.setForeground(textColors[idx]);
        avatar.setBackground(avatarColors[idx]);
        avatar.setOpaque(true);
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setBorder(BorderFactory.createLineBorder(avatarColors[idx].darker(), 1));

        JLabel name = new JLabel(username);
        name.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("SansSerif", Font.PLAIN, 8));
        dot.setForeground(GREEN_COLOR);

        card.add(avatar, BorderLayout.WEST);
        card.add(name, BorderLayout.CENTER);
        card.add(dot, BorderLayout.EAST);

        userListPanel.add(card);
        userListPanel.add(Box.createVerticalStrut(4));
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void handleSave() {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(MessageType.SAVE_REQUEST));
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Whiteboard");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".wbd")) path += ".wbd";
            boolean ok = WhiteboardFileManager.saveFile(path, canvasPanel.getHistory());
            showStatus(ok ? "● Saved: " + file.getName() : "● Save failed.");
        }
    }

    private void handleLoad() {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(MessageType.LOAD_REQUEST));
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load Whiteboard");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            List<DrawingAction> loaded =
                    WhiteboardFileManager.loadFile(file.getAbsolutePath());
            if (loaded != null) {
                canvasPanel.setHistory(loaded);
                showStatus("● Loaded: " + file.getName());
            } else {
                showStatus("● Could not load file.");
            }
        }
    }

    private void connectToServer() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username == null || username.isBlank()) {
            showStatus("● Username cannot be empty.");
            return;
        }
        String host = JOptionPane.showInputDialog(this, "Enter server IP:", "localhost");
        if (host == null || host.isBlank()) {
            showStatus("● Server IP cannot be empty.");
            return;
        }
        client = new WhiteboardClient(this);
        canvasPanel.setClient(client);
        boolean connected = client.connect(host, 9000, username);
        if (connected) {
            showStatus("● Connected as " + username + " · " + host + ":9000");
            penBtn.setEnabled(true);
            eraserBtn.setEnabled(true);
            textBtn.setEnabled(true);
            colorBtn.setEnabled(true);
            undoBtn.setEnabled(true);
            clearBtn.setEnabled(true);
            saveBtn.setEnabled(true);
            loadBtn.setEnabled(true);
            connectBtn.setEnabled(false);
            disconnectBtn.setEnabled(true);
            setActiveTool(penBtn, "✏ Pen tool active");
            canvasPanel.setTool(Tool.PEN);
        } else {
            showStatus("● Connection failed.");
        }
    }

    private void disconnectFromServer() {
        if (client != null) client.disconnect();
        client = null;
        canvasPanel.setClient(null);
        canvasPanel.clearCanvas();
        userListPanel.removeAll();
        userListPanel.revalidate();
        userListPanel.repaint();
        showStatus("● Disconnected.");
        toolLabel.setText("No tool selected");
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
        penBtn.setEnabled(false);
        eraserBtn.setEnabled(false);
        textBtn.setEnabled(false);
        colorBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        loadBtn.setEnabled(false);
        if (activeToolBtn != null) {
            activeToolBtn.setBackground(TOOLBAR_BG);
            activeToolBtn.setForeground(TOOLBAR_TEXT);
            activeToolBtn = null;
        }
    }

    private void setSelectedBrushButton(JButton selected) {
        for (JButton b : brushButtons) {
            b.setBackground(UIManager.getColor("Button.background"));
            b.setForeground(UIManager.getColor("Button.foreground"));
            b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        }
        selected.setBackground(new Color(238, 237, 254));
        selected.setForeground(ACTIVE_COLOR);
        selected.setBorder(BorderFactory.createLineBorder(ACTIVE_COLOR, 1));
    }

    public void addAction(DrawingAction action) {
        canvasPanel.addAction(action);
    }

    public void setHistory(List<DrawingAction> history) {
        canvasPanel.setHistory(history);
    }

    public void updateUserList(List<String> users) {
        userListPanel.removeAll();
        if (users != null) {
            for (String user : users) addUserCard(user);
        }
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    public void showStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
}