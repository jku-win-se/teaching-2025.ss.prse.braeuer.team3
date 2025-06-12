package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import org.postgresql.util.PGobject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SupabaseUploadService {

    private static final String SUPABASE_URL = "https://onvxredsmjqlufgjjojh.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9udnhyZWRzbWpxbHVmZ2pqb2poIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI5OTc5NjYsImV4cCI6MjA1ODU3Mzk2Nn0.1BC54Uq4VaU1V1gkA4TcUyHxlMb8WFlHevq6Vmmh24k";
    private static final String SUPABASE_BUCKET = "rechnung";
    private static final Logger LOGGER = Logger.getLogger(SupabaseUploadService.class.getName());


    /**
     * 1. Datei zu Supabase Storage hochladen
     */
    public static String uploadFile(File file, int userId) {
        try {
            String fileName = "user_" + userId + "_" + System.currentTimeMillis() + "_" + file.getName();
            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
            connection.setRequestProperty("Content-Type", Files.probeContentType(file.toPath()));
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                Files.copy(file.toPath(), os);
            }

            int code = connection.getResponseCode();
            if (code == 200 || code == 201) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Upload successful.");
                }
                return fileName;
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning("Upload failed with code: " + code);
                }
                try (InputStream err = connection.getErrorStream()) {
                    if (err != null) LOGGER.warning(new String(err.readAllBytes()));
                }
                return null;
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IOException during upload", e);
            return null;
        }
    }

    /**
     * 2. Eintrag in die Supabase-Datenbank (Postgres) speichern
     */
    public static boolean saveInvoiceToDatabase(
            int userId,
            String fileUrl,
            String category,
            double invoiceAmount,
            double reimbursementAmount) {

        String sql = """
            INSERT INTO rechnung
                (user_id, file_url, type, invoice_amount, reimbursement_amount, status, upload_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fileUrl);

            // Postgres-Enum rechnung_kategorie setzen
            PGobject typeEnum = new PGobject();
            typeEnum.setType("rechnung_kategorie");
            typeEnum.setValue(category.toUpperCase());
            stmt.setObject(3, typeEnum);

            stmt.setDouble(4, invoiceAmount);
            stmt.setDouble(5, reimbursementAmount);

            // Postgres-Enum rechnung_status setzen
            PGobject statusEnum = new PGobject();
            statusEnum.setType("rechnung_status");
            statusEnum.setValue("SUBMITTED");
            stmt.setObject(6, statusEnum);

            stmt.setObject(7, LocalDate.now());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error while saving invoice to DB", e);
            return false;
        }
    }
}