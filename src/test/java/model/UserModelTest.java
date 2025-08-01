package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die User-Klasse:
 * 1. mustChangePassword-Flag initial und per Setter
 * 2. E-Mail/Name Properties
 */
class UserModelTest {

    @Test
    void testMustChangePasswordFlagDefaultAndSetter() {
        User u = new User();
        // default sollte false sein
        assertFalse(u.isMustChangePassword(), "Neuer User muss mustChangePassword=false haben");

        u.setMustChangePassword(true);
        assertTrue(u.isMustChangePassword(), "Nach setMustChangePassword(true) muss Flag true sein");
    }

    @Test
    void testEmailAndNameProperties() {
        User u = new User();
        u.setEmail("foo@bar.com");
        u.setName("Foo Bar");

        assertEquals("foo@bar.com", u.getEmail(), "getEmail() muss die gesetzte E-Mail zurückgeben");
        assertEquals("Foo Bar", u.getName(),     "getName() muss den gesetzten Namen zurückgeben");

        // über die Properties sollte dasselbe zu holen sein
        assertEquals("foo@bar.com", u.emailProperty().get(), "emailProperty() liefert korrekten Wert");
        assertEquals("Foo Bar", u.nameProperty().get(),      "nameProperty() liefert korrekten Wert");
    }

    @Test
    void notificationPreferences_DefaultValuesCorrect() {
        User user = new User(1, "test@example.com", "hash", "Alice", "USER", false);

        // Default-Werte laut Klasse:
        assertTrue(user.getNotificationPref("INVOICE_APPROVED"));  // Default: true
        assertFalse(user.getNotificationPref("MONTHLY_SUMMARY"));  // Default: false
    }

    @Test
    void setNotificationPref_UpdatesCorrectly() {
        User user = new User();
        user.setNotificationPref("INVOICE_REJECTED", true);
        assertTrue(user.getNotificationPref("INVOICE_REJECTED"));
    }

    @Test
    void passwordChangeFlag_Works() {
        User user = new User();
        user.setMustChangePassword(true);
        assertTrue(user.isMustChangePassword());
    }
}