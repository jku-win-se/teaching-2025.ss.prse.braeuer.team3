package model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private LocalDateTime timestamp;

    public Notification(int id, int userId, String message, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}