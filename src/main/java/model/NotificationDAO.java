package model;

import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    /** Gibt alle Notifications für einen User zurück, sortiert nach Datum absteigend */
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = """
            SELECT id, user_id, message, timestamp
              FROM notification
             WHERE user_id = ?
          ORDER BY timestamp DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                );
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}