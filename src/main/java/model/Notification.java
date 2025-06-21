package model;

import java.time.OffsetDateTime;

public class Notification {
    private final int id;
    private final int userId;
    private final String message;
    private final OffsetDateTime createdAt;
    private final boolean seen;

    public Notification(int id, int userId, String message, OffsetDateTime createdAt, boolean seen) {
        this.id        = id;
        this.userId    = userId;
        this.message   = message;
        this.createdAt = createdAt;
        this.seen      = seen;
    }

    public int getId()                 { return id; }
    public int getUserId()             { return userId; }
    public String getMessage()         { return message; }
    public OffsetDateTime getTimestamp() { return createdAt; }
    public boolean isSeen()            { return seen; }
}