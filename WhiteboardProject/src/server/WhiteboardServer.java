package server;

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

    public WhiteboardServer() {
        clients = Collections.synchronizedList(new ArrayList<>());
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

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client removed. Connected clients: " + clients.size());
    }

    public static void main(String[] args) {
        WhiteboardServer server = new WhiteboardServer();
        server.start();
    }
}