package server;

import model.Message;
import model.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final WhiteboardServer server;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String username;

    public ClientHandler(Socket socket, WhiteboardServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("ClientHandler started for " + socket.getInetAddress());

            while (true) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid object received: " + e.getMessage());
        } finally {
            closeConnection();
            server.removeClient(this);
        }
    }

    private void handleMessage(Message message) {
        if (message == null || message.getType() == null) {
            sendError("Invalid message received");
            return;
        }

        MessageType type = message.getType();

        switch (type) {
            case USER_JOIN:
                handleUserJoin(message);
                break;

            case ACTION:
                handleAction(message);
                break;

            case CLEAR_REQUEST:
                handleClearRequest();
                break;

            case SAVE_REQUEST:
                handleSaveRequest();
                break;

            case LOAD_REQUEST:
                handleLoadRequest();
                break;

            case UNDO_REQUEST:
                handleUndoRequest();
                break;

            default:
                sendError("Unsupported message type: " + type);
                break;
        }
    }

    private void handleSaveRequest() {
        boolean success = model.WhiteboardFileManager.saveFile(
                "server_board.wbd",
                server.getHistory()
        );

        if (success) {
            Message statusMessage = new Message(
                    MessageType.ERROR,
                    "Board saved successfully on server."
            );

            server.broadcast(statusMessage);

            System.out.println("Board saved by " + username);
        } else {
            sendError("Server failed to save board.");
        }
    }

    private void handleLoadRequest() {
        java.util.List<model.DrawingAction> loadedHistory =
                model.WhiteboardFileManager.loadFile("server_board.wbd");

        if (loadedHistory == null) {
            sendError("Server failed to load board. Save file not found or corrupted.");
            return;
        }

        server.replaceHistory(loadedHistory);

        Message historyMessage = new Message(
                MessageType.HISTORY,
                server.getHistory()
        );

        server.broadcast(historyMessage);

        Message statusMessage = new Message(
                MessageType.ERROR,
                "Board loaded successfully from server."
        );

        server.broadcast(statusMessage);

        System.out.println("Board loaded by " + username);
    }

    private void handleUserJoin(Message message) {
        username = message.getUsername();

        if (username == null || username.trim().isEmpty()) {
            username = "User";
        }

        server.addUsername(username);

        Message historyMessage = new Message(
                MessageType.HISTORY,
                server.getHistory()
        );

        sendMessage(historyMessage);

        server.broadcastUserList();

        System.out.println(username + " joined the whiteboard");
    }

    private void handleAction(Message message) {
        if (message.getAction() == null) {
            sendError("Action is empty");
            return;
        }

        server.addAction(message.getAction());
        server.broadcast(message);
    }

    private void handleClearRequest() {
        server.clearHistory();

        Message clearMessage = new Message(
                MessageType.HISTORY,
                server.getHistory()
        );

        server.broadcast(clearMessage);

        System.out.println("Whiteboard cleared by " + username);
    }

    private void handleUndoRequest() {
        boolean success = server.undoLastAction();

        if (!success) {
            sendError("Nothing to undo.");
            return;
        }

        Message historyMessage = new Message(
                MessageType.HISTORY,
                server.getHistory()
        );

        server.broadcast(historyMessage);

        System.out.println("Undo performed by " + username);
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    private void sendError(String text) {
        Message errorMessage = new Message(MessageType.ERROR, text);
        sendMessage(errorMessage);
    }

    public String getUsername() {
        return username;
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Error while closing connection: " + e.getMessage());
        }
    }
}