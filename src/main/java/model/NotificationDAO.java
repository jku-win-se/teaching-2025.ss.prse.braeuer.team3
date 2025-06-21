package model;

import util.DBConnection;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = """
            SELECT id, user_id, message, created_at, seen
              FROM notification
             WHERE user_id = ?
          ORDER BY created_at DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getObject("created_at", OffsetDateTime.class),
                        rs.getBoolean("seen")
                );
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Markiert eine Notification als gelesen */
    public static boolean markAsSeen(int notificationId) {
        String sql = "UPDATE notification SET seen = TRUE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean createNotification(int userId, String msg) {
        String sql = """
            INSERT INTO notification(user_id, message)
            VALUES (?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setString(2, msg);
            return st.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}