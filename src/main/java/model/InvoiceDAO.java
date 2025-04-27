package model;

import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public static List<Invoice> getAllInvoicesByUser(int userId) {
        List<Invoice> invoices = new ArrayList<>();

        String query = "SELECT * FROM rechnung WHERE user_id = ? ORDER BY upload_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getString("file_url"),
                        Invoice.InvoiceCategory.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("amount")
                );
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

    // 🛠 Neue Methode: Update einer Rechnung (nur Kategorie und Betrag z.B.)
    public static boolean updateInvoice(Invoice invoice) {
        String query = "UPDATE rechnung SET type = ?, amount = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, invoice.getCategory().name().toLowerCase());
            stmt.setDouble(2, invoice.getAmount());
            stmt.setInt(3, invoice.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🗑 Neue Methode: Löschen einer Rechnung
    public static boolean deleteInvoice(int invoiceId) {
        String query = "DELETE FROM rechnung WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, invoiceId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ➕ Neue Methode: Neue Rechnung hinzufügen
    public static boolean addInvoice(Invoice invoice, int userId) {
        String query = "INSERT INTO rechnung (user_id, file_url, type, amount, upload_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, invoice.getFileName());
            stmt.setString(3, invoice.getCategory().name().toLowerCase());
            stmt.setDouble(4, invoice.getAmount());
            stmt.setDate(5, Date.valueOf(invoice.getSubmissionDate()));
            stmt.setString(6, invoice.getStatus().name().toLowerCase());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}