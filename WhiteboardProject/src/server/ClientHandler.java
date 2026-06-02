package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final WhiteboardServer server;

    private ObjectOutputStream out;
    private ObjectInputStream in;

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
                Object receivedObject = in.readObject();
                System.out.println("Received from client: " + receivedObject);
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

    public void sendObject(Object object) {
        try {
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to send object: " + e.getMessage());
        }
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