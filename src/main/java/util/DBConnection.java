package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://db.onvxredsmjqlufgjjojh.supabase.co:5432/postgres?user=postgres&password=PRSE2025SS!";
    private static final String USER = "postgres";
    private static final String PASSWORD = "PRSE2025SS!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
