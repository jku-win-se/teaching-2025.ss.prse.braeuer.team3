package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SupabaseUploadService {

    private static final String SUPABASE_URL = "https://onvxredsmjqlufgjjojh.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9udnhyZWRzbWpxbHVmZ2pqb2poIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI5OTc5NjYsImV4cCI6MjA1ODU3Mzk2Nn0.1BC54Uq4VaU1V1gkA4TcUyHxlMb8WFlHevq6Vmmh24k";
    private static final String SUPABASE_BUCKET = "rechnung";

    // 1. Datei zu Supabase hochladen
    public static String uploadFile(File file, int userId) {
        try {
            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + "_" + file.getName();
            String url = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST"); // <<< POST, nicht PUT !!!
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
            connection.setRequestProperty("Content-Type", Files.probeContentType(file.toPath()));
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                Files.copy(file.toPath(), outputStream);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Upload successful.");
                return fileName; // Dateiname wird später als file_url gespeichert
            } else {
                System.out.println("Upload failed with code: " + responseCode);

                try (InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        String error = new String(errorStream.readAllBytes());
                        System.out.println(error);
                    }
                }

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Eintrag in die Supabase-Datenbank machen
    public static boolean saveInvoiceToDatabase(int userId, String fileUrl, String category, double amount) {
        String sql = "INSERT INTO rechnung (user_id, file_url, type, amount, status, upload_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fileUrl);
            stmt.setString(3, category.toUpperCase()); // "restaurant" → "RESTAURANT"
            stmt.setDouble(4, amount);
            stmt.setString(5, "SUBMITTED");
            stmt.setObject(6, LocalDate.now());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}