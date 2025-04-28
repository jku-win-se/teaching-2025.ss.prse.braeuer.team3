package model;

import util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static User validateLogin(String email, String password) {
        String query = "SELECT * FROM benutzer WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("passwort");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            hashedPassword,
                            rs.getString("name"),
                            rs.getString("rolle")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
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

    //  NOVA metoda: Nađi email korisnika po user_id
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
}