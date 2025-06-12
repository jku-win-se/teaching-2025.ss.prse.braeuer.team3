package model;

import util.DBConnection;

import java.sql.*;

public class NotificationSettingsDAO {

    /** Liest die bestehenden Settings für einen User; falls kein Eintrag existiert, erzeugt Default-Settings */
    public static NotificationSettings getSettingsForUser(int userId) {
        String query = "SELECT notify_invoice_approved, notify_invoice_rejected, notify_monthly_summary "
                + "FROM benutzer_notification_settings WHERE benutzer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new NotificationSettings(
                        userId,
                        rs.getBoolean("notify_invoice_approved"),
                        rs.getBoolean("notify_invoice_rejected"),
                        rs.getBoolean("notify_monthly_summary")
                );
            } else {
                // Wenn noch keine Settings, lege Default an
                NotificationSettings defaults = new NotificationSettings(userId, true, true, false);
                insertDefaultSettings(defaults);
                return defaults;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new NotificationSettings(userId, true, true, false);
        }
    }

    private static void insertDefaultSettings(NotificationSettings settings) {
        String insert = "INSERT INTO benutzer_notification_settings "
                + "(benutzer_id, notify_invoice_approved, notify_invoice_rejected, notify_monthly_summary) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, settings.getUserId());
            stmt.setBoolean(2, settings.isNotifyInvoiceApproved());
            stmt.setBoolean(3, settings.isNotifyInvoiceRejected());
            stmt.setBoolean(4, settings.isNotifyMonthlySummary());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Aktualisiert ein einzelnes Setting-Flag */
    public static boolean updateInvoiceApprovedFlag(int userId, boolean value) {
        return updateFlag(userId, "notify_invoice_approved", value);
    }

    public static boolean updateInvoiceRejectedFlag(int userId, boolean value) {
        return updateFlag(userId, "notify_invoice_rejected", value);
    }

    public static boolean updateMonthlySummaryFlag(int userId, boolean value) {
        return updateFlag(userId, "notify_monthly_summary", value);
    }

    private static boolean updateFlag(int userId, String column, boolean value) {
        String sql = "UPDATE benutzer_notification_settings SET " + column + " = ? WHERE benutzer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, value);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
