package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void testSessionManagement() {
        User user = new User(1, "test@example.com", "hash", "Alice", "USER", false);

        Session.setCurrentUser(user);
        assertEquals(user, Session.getCurrentUser());

        Session.clearCurrentUser();
        assertNull(Session.getCurrentUser());
    }
}