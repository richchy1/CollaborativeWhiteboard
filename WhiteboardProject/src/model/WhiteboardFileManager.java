package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardFileManager {

    public static boolean saveFile(String filePath, List<DrawingAction> history) {
        if (history == null) {
            System.out.println("Save error: History list cannot be null.");
            return false;
        }

        File file = new File(filePath);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(history));
            out.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    public static List<DrawingAction> loadFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Load error: The specified save file was not found.");
            return null;
        }
        if (file.length() == 0) {
            System.out.println("Load error: The save file is empty or corrupted.");
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object data = in.readObject();
            if (data instanceof List) {
                return (List<DrawingAction>) data;
            } else {
                System.out.println("Load error: Invalid file format.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load error: " + e.getMessage());
            return null;
        }
    }
}