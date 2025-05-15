package model;

import util.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RefundConfigDAO {

    public Map<String, Double> getCurrentAmounts() {
        Map<String, Double> map = new HashMap<>();
        String query = "SELECT category, amount FROM refund_config";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("category").toUpperCase(), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public double calculateRefundAmount(String category, double invoiceAmount){
        double maxAmount = getCurrentAmounts().getOrDefault(category.toUpperCase(), 0.0);
        return Math.min(invoiceAmount, maxAmount);
    }

    public boolean updateAmount(String category, double amount) {
        String query = "UPDATE refund_config SET amount = ? WHERE category = ?::rechnung_kategorie";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, amount);
            stmt.setString(2, category.toUpperCase());

            int updated = stmt.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
