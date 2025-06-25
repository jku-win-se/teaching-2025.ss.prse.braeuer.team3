package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationSettingsTest {

    @Test
    void settersAndGetters_WorkCorrectly() {
        NotificationSettings settings = new NotificationSettings(1, true, false, true);

        assertEquals(1, settings.getUserId());
        assertTrue(settings.isNotifyInvoiceApproved());
        assertFalse(settings.isNotifyInvoiceRejected());
        assertTrue(settings.isNotifyMonthlySummary());

        // Änderungen testen
        settings.setNotifyMonthlySummary(false);
        assertFalse(settings.isNotifyMonthlySummary());
    }
}