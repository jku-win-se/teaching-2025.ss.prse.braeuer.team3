package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.RefundConfigDAO;


public class ReimbursementSettingsController {

    @FXML
    private TextField restaurantAmountField;

    @FXML
    private TextField supermarketAmountField;

    @FXML
    private void saveReimbursementSettings() {
        try {

            double restaurantAmount = Double.parseDouble(restaurantAmountField.getText());
            double supermarketAmount = Double.parseDouble(supermarketAmountField.getText());


            if (restaurantAmount < 0 || supermarketAmount < 0) {
                showAlert("Amounts must be positive numbers.");
                return;
            }

            RefundConfigDAO dao = new RefundConfigDAO();
            boolean updatedRestaurant = dao.updateAmount("RESTAURANT", restaurantAmount);
            boolean updatedSupermarket = dao.updateAmount("SUPERMARKET", supermarketAmount);

            if (updatedRestaurant && updatedSupermarket) {
                showAlert("Settings saved successfully!");
            } else {
                showAlert("Failed to update settings in database.");
            }

        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reimbursement Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
