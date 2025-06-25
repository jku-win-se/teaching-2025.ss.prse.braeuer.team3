package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Invoice-Klasse:
 * 1. Getter/Setter für invoiceAmount und reimbursementAmount
 * 2. isEditable() abhängig von Monat und Status
 */
class InvoiceTest {

    @Test
    void testInvoiceAmountAndReimbursementSettersGetters() {
        Invoice inv = new Invoice("file.png", Invoice.InvoiceCategory.RESTAURANT, 42.5);
        inv.setReimbursementAmount(3.0);

        assertEquals(42.5, inv.getInvoiceAmount(), 1e-6, "invoiceAmount muss zurückgegeben werden");
        assertEquals(3.0, inv.getReimbursementAmount(), 1e-6, "reimbursementAmount muss zurückgegeben werden");
    }

    @Test
    void testIsEditableCurrentMonthNotApproved() {
        Invoice inv = new Invoice("f.png", Invoice.InvoiceCategory.SUPERMARKET, 10.0);
        inv.setSubmissionDate(LocalDate.now());
        inv.setStatus(Invoice.InvoiceStatus.SUBMITTED);

        assertTrue(inv.isEditable(), "Invoice im aktuellen Monat und noch nicht approved muss editierbar sein");
    }

    @Test
    void testIsEditablePastMonth() {
        Invoice inv = new Invoice("f.png", Invoice.InvoiceCategory.RESTAURANT, 5.0);
        inv.setSubmissionDate(LocalDate.now().minusMonths(1));
        inv.setStatus(Invoice.InvoiceStatus.SUBMITTED);

        assertFalse(inv.isEditable(), "Invoice aus vergangenem Monat darf nicht editierbar sein");
    }



        @Test
        void testInvoiceInitialization() {
            Invoice invoice = new Invoice("test.pdf", Invoice.InvoiceCategory.RESTAURANT, 100.0);
            assertEquals("test.pdf", invoice.getFileName());
            assertEquals(100.0, invoice.getInvoiceAmount());
            assertEquals(0.0, invoice.getReimbursementAmount()); // Default-Wert
        }

        @Test
        void isEditable_CurrentMonthAndSubmitted_ReturnsTrue() {
            Invoice invoice = new Invoice("test.pdf", Invoice.InvoiceCategory.RESTAURANT, 100.0);
            invoice.setSubmissionDate(LocalDate.now());
            invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
            assertTrue(invoice.isEditable());
        }

        @Test
        void isEditable_RejectedStatus_ReturnsFalse() {
            Invoice invoice = new Invoice("test.pdf", Invoice.InvoiceCategory.SUPERMARKET, 50.0);
            invoice.setSubmissionDate(LocalDate.now());
            invoice.setStatus(Invoice.InvoiceStatus.REJECTED);
            assertFalse(invoice.isEditable());
        }

        @Test
        void isEditable_OldMonth_ReturnsFalse() {
            Invoice invoice = new Invoice("old.pdf", Invoice.InvoiceCategory.RESTAURANT, 75.0);
            invoice.setSubmissionDate(LocalDate.now().minusMonths(1));
            invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
            assertFalse(invoice.isEditable());
        }

        @Test
        void testStarredProperty() {
            Invoice invoice = new Invoice("starred.pdf", Invoice.InvoiceCategory.RESTAURANT, 200.0);
            invoice.setStarred(true);
            assertTrue(invoice.isStarred());
        }

    @Test
    void testEmailAndCategory() {
        Invoice invoice = new Invoice("demo.pdf", Invoice.InvoiceCategory.SUPERMARKET, 50.0);
        invoice.setEmail("user@example.com");
        assertEquals("user@example.com", invoice.getEmail());

        invoice.setCategory(Invoice.InvoiceCategory.RESTAURANT);
        assertEquals(Invoice.InvoiceCategory.RESTAURANT, invoice.getCategory());
    }


}
