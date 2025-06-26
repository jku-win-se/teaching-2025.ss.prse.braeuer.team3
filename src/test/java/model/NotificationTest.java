package model;

import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void constructor_SetsAllFieldsCorrectly() {
        OffsetDateTime now = OffsetDateTime.now();
        Notification notification = new Notification(1, 100, "Test Message", now, false);

        assertEquals(1, notification.getId());
        assertEquals(100, notification.getUserId());
        assertEquals("Test Message", notification.getMessage());
        assertEquals(now, notification.getTimestamp());
        assertFalse(notification.isSeen());
    }
}