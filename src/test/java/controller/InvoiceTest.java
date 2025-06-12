package controller;

import model.Invoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void testInvoiceAmountSetterGetter() {
        Invoice inv = new Invoice("file.png", Invoice.InvoiceCategory.RESTAURANT, 12.34);
        assertEquals(12.34, inv.getInvoiceAmount());
        inv.setInvoiceAmount(5.0);
        assertEquals(5.0, inv.getInvoiceAmount());
    }

    @Test
    void testReimbursementSetterGetter() {
        Invoice inv = new Invoice("file.png", Invoice.InvoiceCategory.SUPERMARKET, 8.0);
        assertEquals(0.0, inv.getReimbursementAmount());
        inv.setReimbursementAmount(2.5);
        assertEquals(2.5, inv.getReimbursementAmount());
    }

    @Test
    void testIsEditable() {
        Invoice inv = new Invoice("file.png", Invoice.InvoiceCategory.RESTAURANT, 10.0);
        LocalDate today = LocalDate.now();
        inv.setSubmissionDate(today);
        inv.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        assertTrue(inv.isEditable(), "heutige, nicht-approved Rechnung sollte editierbar sein");

        inv.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        inv.setSubmissionDate(today.minusMonths(1));
        assertFalse(inv.isEditable(), "Monat zurückliegende Rechnung darf nicht editierbar sein");
    }

    @Test
    void testPropertyBindings() {
        Invoice inv = new Invoice("f", Invoice.InvoiceCategory.SUPERMARKET, 3.21);
        assertEquals("f", inv.fileNameProperty().get());
        assertEquals(Invoice.InvoiceCategory.SUPERMARKET, inv.categoryProperty().get());
        assertEquals(3.21, inv.invoiceAmountProperty().get());
    }
}
