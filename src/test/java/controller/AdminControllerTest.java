

package controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Invoice;
import model.InvoiceDAO;
import model.UserDAO;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class AdminControllerTest {

    private InvoiceDAO invoiceDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();
    }

    // US6 - Testiranje prikaza svih faktura
    @Test
    void testLoadAllInvoices() {
        List<Invoice> invoices = invoiceDAO.findAllInvoices();
        assertNotNull(invoices, "Invoices list should not be null.");
        assertTrue(invoices.size() >= 0, "Should return 0 or more invoices.");
    }

    // US6 - Testiranje reject funkcionalnosti
    @Test
    void testRejectSubmittedInvoice() {
        int invoiceId = 2; // zameni sa ID fakure koja ima status SUBMITTED
        boolean success = invoiceDAO.updateInvoiceStatus(invoiceId, Invoice.InvoiceStatus.REJECTED);
        assertTrue(success, "Should successfully reject a submitted invoice.");
    }

    // US6 - Testiranje brisanja fakture
   /* @Test
    void testDeleteInvoice() {

    }
*/

    // US7 - Testiranje eksportovanja CSV
    @Test
    void testExportCSV() {
        List<Invoice> invoices = invoiceDAO.findAllInvoices();
        File file = new File("test_invoices.csv");

        try (var writer = new java.io.FileWriter(file)) {
            writer.write("Email,Date,Amount,Classification,Status\n");
            for (Invoice invoice : invoices) {
                writer.write(invoice.getEmail() + "," +
                        invoice.getSubmissionDate() + "," +
                        invoice.getAmount() + "," +
                        invoice.getCategory() + "," +
                        invoice.getStatus() + "\n");
            }
        } catch (Exception e) {
            fail("Export CSV failed due to exception: " + e.getMessage());
        }

        assertTrue(file.exists(), "Exported CSV file should exist.");
    }

    // US7 - Testiranje eksportovanja Payroll JSON
    @Test
    void testExportPayrollJSON() {
        List<Invoice> invoices = invoiceDAO.findAllInvoices();
        File file = new File("test_payroll.json");

        try (var writer = new java.io.FileWriter(file)) {
            writer.write("[\n");
            for (int i = 0; i < invoices.size(); i++) {
                Invoice invoice = invoices.get(i);
                writer.write("  {\n");
                writer.write("    \"email\": \"" + invoice.getEmail() + "\",\n");
                writer.write("    \"amount\": " + invoice.getAmount() + "\n");
                if (i < invoices.size() - 1) {
                    writer.write("  },\n");
                } else {
                    writer.write("  }\n");
                }
            }
            writer.write("]\n");
        } catch (Exception e) {
            fail("Export Payroll JSON failed due to exception: " + e.getMessage());
        }

        assertTrue(file.exists(), "Exported Payroll JSON file should exist.");
    }

    // US7 - Testiranje kada nema faktura za export
    @Test
    void testNoInvoicesForEmptyMonth() {
        List<Invoice> invoices = invoiceDAO.findInvoicesByMonth(2023, 1); // neki mesec bez faktura

        assertTrue(invoices.isEmpty(), "Invoices list should be empty for this month.");
    }
}
