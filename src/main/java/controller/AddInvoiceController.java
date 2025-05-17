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

    @FXML private ImageView uploadIcon;
    @FXML private Button chooseFileButton;
    @FXML private Label fileLabel;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton supermarketRadio;
    @FXML private TextField invoiceAmountField;
    @FXML private Label reimbursementAmountLabel;
    @FXML private Button submitButton;

    private ToggleGroup categoryGroup; // NICHT @FXML!
    private File selectedFile;

    @FXML
    public void initialize() {
        uploadIcon.setImage(new Image(getClass().getResourceAsStream("/images/upload_icon.png")));

        // ToggleGroup initialisieren und zuweisen
        categoryGroup = new ToggleGroup();
        restaurantRadio.setToggleGroup(categoryGroup);
        supermarketRadio.setToggleGroup(categoryGroup);

        // Live-Update der Erstattung
        invoiceAmountField.textProperty().addListener((obs, oldVal, newVal) -> updateReimbursement());
        categoryGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateReimbursement());
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
        if (selectedFile == null || categoryGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a file and a category.");
            return;
        }

        try {
            double invoiceAmount = Double.parseDouble(invoiceAmountField.getText());
            String category = getSelectedCategory();
            int userId = Session.getCurrentUser().getId();

            double reimbursementAmount = calculateReimbursement(invoiceAmount, category);

            // Datei zu Supabase hochladen
            String fileUrl = SupabaseUploadService.uploadFile(selectedFile, userId);

            if (fileUrl != null) {
                boolean success = SupabaseUploadService.saveInvoiceToDatabase(
                        userId, fileUrl, category, invoiceAmount, reimbursementAmount);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Invoice uploaded and saved successfully.");
                    closeWindow();
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

    private void updateReimbursement() {
        try {
            double invoice = Double.parseDouble(invoiceAmountField.getText());
            String category = getSelectedCategory();
            double reimbursement = calculateReimbursement(invoice, category);
            reimbursementAmountLabel.setText(String.format("%.2f €", reimbursement));
        } catch (Exception e) {
            reimbursementAmountLabel.setText("-");
        }
    }

    private String getSelectedCategory() {
        if (restaurantRadio.isSelected()) return "restaurant";
        if (supermarketRadio.isSelected()) return "supermarket";
        return "";
    }

    private double calculateReimbursement(double amount, String category) {
        return switch (category.toLowerCase()) {
            case "restaurant" -> Math.min(amount, 3.0);
            case "supermarket" -> Math.min(amount, 2.5);
            default -> 0.0;
        };
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