package model;

import util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /** Validiert Login und liest das „must_change_password“-Flag aus */
    public static User validateLogin(String email, String password) {
        String query = "SELECT * FROM benutzer WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("passwort");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    User u = new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            hashedPassword,
                            rs.getString("name"),
                            rs.getString("rolle"),
                            rs.getBoolean("must_change_password")
                    );
                    return u;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Prüft, ob eine E-Mail bereits registriert ist */
    public static boolean emailExists(String email) {
        String query = "SELECT 1 FROM benutzer WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Liefert die E-Mail zu einer Benutzer-ID */
    public String findEmailByBenutzerId(int userId) {
        String email = null;
        String query = "SELECT email FROM benutzer WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    /** Holt alle User und das Password-Change-Flag */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM benutzer";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("passwort"),
                        rs.getString("name"),
                        rs.getString("rolle"),
                        rs.getBoolean("must_change_password")
                );
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /** Legt einen neuen User mit Default-Passwort und Flag=true an */
    public static boolean addUser(String name, String email, String role) {
        String defaultHash = BCrypt.hashpw("default123", BCrypt.gensalt());
        String sql = """
            INSERT INTO benutzer(name, email, rolle, passwort, must_change_password)
            VALUES (?, ?, ?, ?, TRUE)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, name);
            st.setString(2, email);
            st.setString(3, role);
            st.setString(4, defaultHash);
            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Setzt neues Passwort und deaktiviert das Änderungs-Flag */
    public static boolean updatePasswordAndClearFlag(int userId, String newHash) {
        String sql = """
            UPDATE benutzer
               SET passwort = ?, must_change_password = FALSE
             WHERE id = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {

            st.setString(1, newHash);
            st.setInt(2, userId);
            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Löscht einen User anhand der E-Mail */
    public static boolean deleteUserByEmail(String email) {
        String query = "DELETE FROM benutzer WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ────────────────────────────────────────────────────────
    // Neue Methoden für Notification Preferences
    // ────────────────────────────────────────────────────────

    /**
     * Liest aus, ob der User-Typ für diese Notification enabled oder disabled ist.
     * Gibt bei erstmaligem Zugriff Defaultwerte zurück:
     *   - Alle Notification-Typen ON, außer "MONTHLY_SUMMARY" → OFF.
     */
    public static boolean getNotificationPref(int userId, String type) {
        String sql = """
            SELECT enabled
              FROM notification_preferences
             WHERE user_id = ? AND type = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            st.setString(2, type);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("enabled");
            } else {
                return !"MONTHLY_SUMMARY".equalsIgnoreCase(type);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Speichert oder aktualisiert die Notification-Einstellung für den User.
     * Verwendet UPSERT (ON CONFLICT DO UPDATE).
     */
    public static boolean updateNotificationPref(int userId, String type, boolean enabled) {
        String sql = """
            INSERT INTO notification_preferences(user_id, type, enabled)
            VALUES(?, ?, ?)
            ON CONFLICT (user_id, type)
              DO UPDATE SET enabled = EXCLUDED.enabled
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            st.setString(2, type);
            st.setBoolean(3, enabled);
            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}