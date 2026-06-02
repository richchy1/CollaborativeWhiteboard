package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WhiteboardClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected to server: " + host + ":" + port);

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    public void sendObject(Object object) {
        try {
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("Disconnected from server");

        } catch (IOException e) {
            System.out.println("Disconnect error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        WhiteboardClient client = new WhiteboardClient();
        client.connect("localhost", 5000);
        client.sendObject("Hello from client");
    }
}