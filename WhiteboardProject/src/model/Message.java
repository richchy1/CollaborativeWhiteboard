package model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private DrawingAction action;
    private List<DrawingAction> history;
    private String username;
    private List<String> users;
    private String text;

    public Message() {}

    public Message(MessageType type) {
        this.type = type;
    }

    // Getters and Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public DrawingAction getAction() { return action; }
    public void setAction(DrawingAction action) { this.action = action; }

    public List<DrawingAction> getHistory() { return history; }
    public void setHistory(List<DrawingAction> history) { this.history = history; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getUsers() { return users; }
    public void setUsers(List<String> users) { this.users = users; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}