package model;

import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    /** (Admin) Holt alle Rechnungen ohne Filter */
    public List<Invoice> findAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = """
            SELECT id, user_id, file_url, type, invoice_amount, reimbursement_amount, status, upload_date
              FROM rechnung
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                inv.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                inv.setId(rs.getInt("id"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                inv.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                invoices.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /** Holt alle invoices des Users (mit Beträgen) */
    public static List<Invoice> getAllInvoicesByUser(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = """
            SELECT id, file_url, type, invoice_amount, reimbursement_amount, status, upload_date
              FROM rechnung
             WHERE user_id = ?
          ORDER BY upload_date DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                inv.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                inv.setId(rs.getInt("id"));
                inv.setUserId(userId);
                inv.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                inv.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                invoices.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /** Holt die zuletzt 4 als "starred" markierten Rechnungen */
    public static List<Invoice> getStarredInvoicesByUser(int userId) {
        List<Invoice> starred = new ArrayList<>();
        String sql = """
            SELECT r.id, r.file_url, r.type, r.invoice_amount, r.reimbursement_amount, r.status, r.upload_date
              FROM rechnung r
              JOIN benutzer_rechnung_starred br ON r.id = br.rechnung_id
             WHERE br.benutzer_id = ?
          ORDER BY br.starred_date DESC
             LIMIT 4
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                inv.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                inv.setId(rs.getInt("id"));
                inv.setUserId(userId);
                inv.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                inv.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                starred.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return starred;
    }

    /** Holt die zuletzt 4 hochgeladenen/viewed Rechnungen */
    public static List<Invoice> getRecentInvoicesByUser(int userId) {
        List<Invoice> recent = new ArrayList<>();
        String sql = """
            SELECT id, file_url, type, invoice_amount, reimbursement_amount, status, upload_date
              FROM rechnung
             WHERE user_id = ?
          ORDER BY upload_date DESC
             LIMIT 4
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                inv.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                inv.setId(rs.getInt("id"));
                inv.setUserId(userId);
                inv.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                inv.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                recent.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recent;
    }

    /** Markiert oder entmarkiert eine Rechnung als "starred" */
    public static boolean toggleStar(int userId, int invoiceId, boolean star) {
        String sql = star
                ? "INSERT INTO benutzer_rechnung_starred (benutzer_id, rechnung_id) VALUES (?,?) ON CONFLICT DO NOTHING"
                : "DELETE FROM benutzer_rechnung_starred WHERE benutzer_id = ? AND rechnung_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, invoiceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateInvoice(Invoice invoice) {
        String sql = """
            UPDATE rechnung
               SET type           = ?::rechnung_kategorie,
                   invoice_amount = ?
             WHERE id             = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoice.getCategory().name());
            stmt.setDouble(2, invoice.getInvoiceAmount());
            stmt.setInt(3, invoice.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteInvoice(int invoiceId) {
        String sql = "DELETE FROM rechnung WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addInvoice(Invoice invoice, int userId) {
        String sql = """
            INSERT INTO rechnung
                (user_id, file_url, type, invoice_amount, reimbursement_amount, upload_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public boolean updateInvoiceStatus(int invoiceId, Invoice.InvoiceStatus status) {
        String sql = "UPDATE rechnung SET status = ?::rechnung_status WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = """
            SELECT id, user_id, file_url, type, invoice_amount, reimbursement_amount, status, upload_date
              FROM rechnung
             WHERE EXTRACT(YEAR FROM upload_date) = ?
               AND EXTRACT(MONTH FROM upload_date) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("invoice_amount")
                );
                inv.setReimbursementAmount(rs.getDouble("reimbursement_amount"));
                inv.setId(rs.getInt("id"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setSubmissionDate(rs.getDate("upload_date").toLocalDate());
                inv.setStatus(Invoice.InvoiceStatus.valueOf(rs.getString("status").toUpperCase()));
                invoices.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }
}
