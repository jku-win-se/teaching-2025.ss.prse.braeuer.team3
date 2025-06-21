package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für das Entfernen führender Slashes von Dateinamen,
 * wie in RequestHistoryController.showInvoicePopup verwendet.
 */
class FileNameTrimTest {

    @Test
    void testLeadingSlashRemovalSingle() {
        String raw = "/invoice123.png";
        String trimmed = raw.replaceAll("^/+", "");
        assertEquals("invoice123.png", trimmed, "Ein führender Slash muss entfernt werden");
    }

    @Test
    void testLeadingSlashRemovalMultiple() {
        String raw = "///nested/path/invoice.pdf";
        String trimmed = raw.replaceAll("^/+", "");
        assertEquals("nested/path/invoice.pdf", trimmed, "Alle führenden Slashes müssen entfernt werden");
    }

    @Test
    void testNoLeadingSlash() {
        String raw = "clean.png";
        String trimmed = raw.replaceAll("^/+", "");
        assertEquals("clean.png", trimmed, "Ohne führende Slash darf nichts verändert werden");
    }
}
