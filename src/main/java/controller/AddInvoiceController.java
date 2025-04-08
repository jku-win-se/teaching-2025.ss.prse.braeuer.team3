package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddInvoiceController {

    @FXML private Button uploadButton;
    @FXML private Button submitButton;
    @FXML private Label fileNameLabel;
    @FXML private Label errorLabel;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton supermarketRadio;
    @FXML private TextField amountField;
    @FXML private Label reimbursementLabel;


    private File selectedFile;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleFileChoose() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Invoice");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Files", "*.jpg", "*.jpeg", "*.png", "*.pdf")
        );

        selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            errorLabel.setVisible(false);
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSubmit() {
        errorLabel.setVisible(false);

        if (selectedFile == null) {
            showError("Please select a file.");
            return;
        }

        String fileName = selectedFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".pdf"))) {
            showError("Unsupported file format. Only JPG, PNG or PDF allowed.");
            return;
        }

        if (!(restaurantRadio.isSelected() || supermarketRadio.isSelected())) {
            showError("Please select a category (Restaurant or Supermarket).");
            return;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showError("Please enter the total amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showError("Invalid amount. Please enter a number.");
            return;
        }

        String category = restaurantRadio.isSelected() ? "Restaurant" : "Supermarket";
        double reimbursement = calculateReimbursement(category, amount);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));


        boolean submissionSuccess = true; // ← Simuliert erfolgreichen Upload

        if (submissionSuccess) {
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Submission Successful");
            confirmation.setHeaderText("Invoice submitted successfully");
            confirmation.setContentText(
                    "✅ Reimbursement amount: €" + reimbursement +
                            "\n📅 Submission date: " + today
            );
            confirmation.show();

            // Schließen
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();

        } else {
            showError("Upload failed. Please try again.");
        }
    }

    public static double calculateReimbursement(String type, double amount) {
        if ("Restaurant".equalsIgnoreCase(type)) {
            return amount >= 3.0 ? 3.0 : amount;
        } else if ("Supermarket".equalsIgnoreCase(type)) {
            return amount >= 2.5 ? 2.5 : amount;
        } else {
            return 0.0; // Invalid category
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}