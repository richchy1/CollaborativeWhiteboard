package server;

import model.DrawingAction;
import model.Message;
import model.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhiteboardServer {
    private static final int DEFAULT_PORT = 5000;

    private ServerSocket serverSocket;

    private final List<ClientHandler> clients;
    private final List<String> usernames;
    private final List<DrawingAction> history;

    public WhiteboardServer() {
        clients = Collections.synchronizedList(new ArrayList<>());
        usernames = Collections.synchronizedList(new ArrayList<>());
        history = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server started on port " + DEFAULT_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void addUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            usernames.add(username);
            System.out.println("User joined: " + username);
        }
    }

    public void removeUsername(String username) {
        if (username != null) {
            usernames.remove(username);
            System.out.println("User left: " + username);
        }
    }

    public void addAction(DrawingAction action) {
        if (action != null) {
            history.add(action);
        }
    }

    public List<DrawingAction> getHistory() {
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    public void clearHistory() {
        history.clear();
    }

    public void broadcast(Message message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public void broadcastUserList() {
        Message message = new Message(MessageType.USER_LIST);

        synchronized (usernames) {
            message.setUsers(new ArrayList<>(usernames));
        }

        broadcast(message);
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);

        String username = clientHandler.getUsername();
        removeUsername(username);

        broadcastUserList();

        System.out.println("Client removed. Connected clients: " + clients.size());
    }

    public static void main(String[] args) {
        WhiteboardServer server = new WhiteboardServer();
        server.start();
    }
}