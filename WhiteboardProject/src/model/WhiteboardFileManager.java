package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardFileManager {

    public static void saveHistory(File file, List<DrawingAction> history) throws IOException {
        if (history == null) {
            throw new IllegalArgumentException("History list cannot be null.");
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(history)); // Wrap to ensure serializable container list
            out.flush();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<DrawingAction> loadHistory(File file) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("The specified save file was not found.");
        }
        if (file.length() == 0) {
            throw new IOException("The save file is empty or corrupted.");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object data = in.readObject();
            if (data instanceof List) {
                return (List<DrawingAction>) data;
            } else {
                throw new IOException("Invalid file format: Object is not a valid whiteboard history.");
            }
        }
    }
}