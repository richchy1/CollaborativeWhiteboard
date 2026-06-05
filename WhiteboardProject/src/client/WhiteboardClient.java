package client;

import gui.WhiteboardGUI;
import model.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WhiteboardClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private WhiteboardGUI gui;
    private Thread receiverThread;
    private boolean manuallyDisconnected = false;

    public WhiteboardClient(WhiteboardGUI gui) {
        this.gui = gui;
    }

    public boolean connect(String host, int port, String username) {
        try {
            manuallyDisconnected = false;
            socket = new Socket(host, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());

            Message joinMessage = new Message(MessageType.USER_JOIN);
            joinMessage.setUsername(username);
            sendMessage(joinMessage);

            listenForServerMessages();

            System.out.println("Connected to server: " + host + ":" + port);

            return true;

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());

            if (gui != null) {
                SwingUtilities.invokeLater(() ->
                        gui.showStatus("Connection error: " + e.getMessage())
                );
            }

            return false;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void sendAction(DrawingAction action) {
        Message message = new Message(MessageType.ACTION, action);
        sendMessage(message);
    }

    public void sendMessage(Message message) {
        if (!isConnected()) {
            if (gui != null) {
                SwingUtilities.invokeLater(() ->
                        gui.showStatus("Not connected to server.")
                );
            }
            return;
        }

        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());

            if (gui != null) {
                SwingUtilities.invokeLater(() ->
                        gui.showStatus("Send error: " + e.getMessage())
                );
            }
        }
    }

    private void listenForServerMessages() {
        receiverThread = new Thread(() -> {
            try {
                while (isConnected()) {
                    Object object = in.readObject();

                    if (!(object instanceof Message)) {
                        continue;
                    }

                    Message message = (Message) object;
                    handleMessage(message);
                }

            } catch (IOException e) {
                System.out.println("Disconnected from server: " + e.getMessage());

                if (gui != null) {
                    SwingUtilities.invokeLater(() ->
                            gui.showStatus("Disconnected from server.")
                    );
                }

                if (!manuallyDisconnected && gui != null) {
                    SwingUtilities.invokeLater(() ->
                            gui.showStatus("Disconnected from server.")
                    );
                }

            } catch (ClassNotFoundException e) {
                System.out.println("Unknown message received: " + e.getMessage());

                if (gui != null) {
                    SwingUtilities.invokeLater(() ->
                            gui.showStatus("Invalid message from server.")
                    );
                }
            } finally {
                disconnect();
            }
        });

        receiverThread.start();
    }

    private void handleMessage(Message message) {
        if (gui == null || message == null || message.getType() == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case ACTION:
                    gui.addAction(message.getAction());
                    break;

                case HISTORY:
                    gui.setHistory(message.getHistory());
                    break;

                case USER_LIST:
                    gui.updateUserList(message.getUsers());
                    break;

                case ERROR:
                    gui.showStatus(message.getText());
                    break;

                default:
                    break;
            }
        });
    }

    public void disconnect() {
        try {
            manuallyDisconnected = true;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("Disconnected from server");

        } catch (IOException e) {
            System.out.println("Disconnect error: " + e.getMessage());
        }
    }
}