package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Session;
import util.SupabaseUploadService;

import java.io.File;

public class AddInvoiceController {

    @FXML
    private ImageView uploadIcon;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Label fileLabel;

    @FXML
    private RadioButton restaurantRadio;

    @FXML
    private RadioButton supermarketRadio;

    @FXML
    private TextField amountField;

    private File selectedFile;

    @FXML
    private Button submitButton;

    @FXML
    public void initialize() {
        uploadIcon.setImage(new Image(getClass().getResourceAsStream("/images/upload_icon.png"))); // Dein Icon
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Invoice File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());

        if (file != null) {
            selectedFile = file;
            fileLabel.setText(file.getName());
        }
    }

    @FXML
    private void handleSubmit() {
        if (selectedFile == null || (!restaurantRadio.isSelected() && !supermarketRadio.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Please select a file and a category.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = restaurantRadio.isSelected() ? "restaurant" : "supermarket";

            int userId = Session.getCurrentUser().getId();

            // 1. Datei zu Supabase Storage hochladen
            String fileUrl = SupabaseUploadService.uploadFile(selectedFile, userId);

            if (fileUrl != null) {
                // 2. Eintrag in die Datenbank speichern
                boolean success = SupabaseUploadService.saveInvoiceToDatabase(userId, fileUrl, category, amount);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Invoice uploaded and saved successfully.");
                    closeWindow(); // Fenster schließen
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to save invoice to database.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to upload file to Supabase.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount entered. Please enter a valid number.");
        }
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upload Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your invoice was uploaded successfully!");
        alert.showAndWait();
    }


    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Upload");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}