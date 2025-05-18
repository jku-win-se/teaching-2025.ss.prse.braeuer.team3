package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public static List<Invoice> getAllInvoicesByUser(int userId) {
        List<Invoice> invoices = new ArrayList<>();

        String query = """
            SELECT id,
                   file_url,
                   type,
                   invoice_amount,
                   reimbursement_amount,
                   status,
                   upload_date
              FROM rechnung
             WHERE user_id = ?
          ORDER BY upload_date DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                invoice.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                invoice.setId(rs.getInt("id"));
                invoice.setDate(rs.getDate("upload_date").toLocalDate());
                invoice.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                invoice.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));

                invoices.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    public static boolean updateInvoice(Invoice invoice) {
        // Wir casten hier den String direkt in dein Postgres-Enum rechnung_kategorie
        String query = """
        UPDATE rechnung
           SET type   = ?::rechnung_kategorie,
               invoice_amount = ?
         WHERE id     = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 1) Enum-Name als String (z.B. "RESTAURANT")
            stmt.setString(1, invoice.getCategory().name());
            // 2) Neuer Betrag
            stmt.setDouble(2, invoice.getInvoiceAmount());
            // 3) ID der zu ändernden Rechnung
            stmt.setInt(3, invoice.getId());

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteInvoice(int invoiceId) {
        String query = "DELETE FROM rechnung WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addInvoice(Invoice invoice, int userId) {
        String query = """
            INSERT INTO rechnung
                (user_id, file_url, type, invoice_amount, reimbursement_amount, upload_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, invoice.getFileName());
            stmt.setString(3, invoice.getCategory().name().toLowerCase());
            stmt.setDouble(4, invoice.getInvoiceAmount());
            stmt.setDouble(5, invoice.getReimbursementAmount());
            stmt.setDate(6, Date.valueOf(invoice.getSubmissionDate()));
            stmt.setString(7, invoice.getStatus().name().toLowerCase());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Holt alle Invoices für den Admin (ohne WHERE) */
    public List<Invoice> findAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String query = """
            SELECT id,
                   user_id,
                   file_url,
                   type,
                   invoice_amount,
                   reimbursement_amount,
                   status,
                   upload_date
              FROM rechnung
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                invoice.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                invoice.setId(rs.getInt("id"));
                invoice.setUserId(rs.getInt("user_id"));
                invoice.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                invoice.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public boolean updateInvoiceStatus(int invoiceId, Invoice.InvoiceStatus status) {
        String query = "UPDATE rechnung SET status = ?::rechnung_status WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, invoiceId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Invoice> findInvoicesByMonth(int year, int month) {
        List<Invoice> invoices = new ArrayList<>();
        String query = """
            SELECT id,
                   user_id,
                   file_url,
                   type,
                   invoice_amount,
                   reimbursement_amount,
                   status,
                   upload_date
              FROM rechnung
             WHERE EXTRACT(YEAR FROM upload_date) = ?
               AND EXTRACT(MONTH FROM upload_date) = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                invoice.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                invoice.setId(rs.getInt("id"));
                invoice.setUserId(rs.getInt("user_id"));
                invoice.setDate(rs.getDate("upload_date").toLocalDate());
                invoice.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                invoice.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

}
