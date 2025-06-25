package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

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