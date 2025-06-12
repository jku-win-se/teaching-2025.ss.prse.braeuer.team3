package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Invoice-Klasse:
 * 1. Getter/Setter für invoiceAmount und reimbursementAmount
 * 2. isEditable() abhängig von Monat und Status
 */
class InvoiceModelTest {

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
}