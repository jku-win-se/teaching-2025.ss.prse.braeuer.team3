
package controller;

import model.Session;
import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für Session:
 * 1. Setzen und Abrufen des aktuellen Nutzers
 * 2. Löschen des aktuellen Nutzers
 */
class SessionTest {

    @Test
    void testSetAndGetCurrentUser() {
        User u = new User();
        Session.setCurrentUser(u);
        assertSame(u, Session.getCurrentUser(), "Session.getCurrentUser() muss den gesetzten User zurückgeben");
    }

    @Test
    void testClearCurrentUser() {
        Session.setCurrentUser(new User());
        Session.clearCurrentUser();
        assertNull(Session.getCurrentUser(), "Nach clearCurrentUser() darf kein User mehr gesetzt sein");
    }
}