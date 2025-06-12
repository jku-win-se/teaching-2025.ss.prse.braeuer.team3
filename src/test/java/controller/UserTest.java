package controller;

import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testMustChangePasswordFlag() {
        User u = new User();
        assertFalse(u.isMustChangePassword(), "Default sollte false sein");
        u.setMustChangePassword(true);
        assertTrue(u.isMustChangePassword());
        u.setMustChangePassword(false);
        assertFalse(u.isMustChangePassword());
    }

    @Test
    void testProperties() {
        User u = new User(7, "bob@example.com", "hash", "Bob", "user", false);
        assertEquals(7, u.getId());
        assertEquals("bob@example.com", u.getEmail());
        assertEquals("hash", u.getPassword());
        assertEquals("Bob", u.getName());
        assertEquals("user", u.getRolle());
        assertFalse(u.isMustChangePassword());
    }
}
