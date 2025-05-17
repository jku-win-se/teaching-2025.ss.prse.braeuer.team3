package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Controller klasa za podešavanje iznosa refundacije.
 */
public class ReimbursementSettingsController {

    @FXML
    private TextField restaurantAmountField;

    @FXML
    private TextField supermarketAmountField;

    /**
     * Poziva se kada admin klikne na "Save Reimbursement Settings".
     */
    @FXML
    private void saveReimbursementSettings() {
        try {
            // Pročitaj unesene vrednosti
            double restaurantAmount = Double.parseDouble(restaurantAmountField.getText());
            double supermarketAmount = Double.parseDouble(supermarketAmountField.getText());

            // Prosta validacija
            if (restaurantAmount < 0 || supermarketAmount < 0) {
                showAlert("Amounts must be positive numbers.");
                return;
            }

            // (Privremeno) čuvamo u lokalni fajl – možeš ovo kasnije zameniti sa Supabase logikom
            try (FileWriter writer = new FileWriter("reimbursement_config.txt")) {
                writer.write("restaurant=" + restaurantAmount + "\n");
                writer.write("supermarket=" + supermarketAmount + "\n");
            }

            showAlert("Settings saved successfully!");

        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers.");
        } catch (IOException e) {
            showAlert("Error saving settings.");
            e.printStackTrace();
        }
    }

    /**
     * Pomoćna metoda za prikaz poruke.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reimbursement Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
