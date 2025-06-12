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
    @FXML private ToggleGroup categoryGroup;
    @FXML private TextField invoiceAmountField;
    @FXML private Label reimbursementAmountLabel;

    private File selectedFile;

    @FXML
    public void initialize() {
        uploadIcon.setImage(new Image(getClass().getResourceAsStream("/images/upload_icon.png")));
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
        if (selectedFile == null || (!restaurantRadio.isSelected() && !supermarketRadio.isSelected())) {
            showAlert(Alert.AlertType.WARNING, "Please select a file and a category.");
            return;
        }
        try {
            double invoiceAmount = Double.parseDouble(invoiceAmountField.getText());
            String category = restaurantRadio.isSelected() ? "restaurant" : "supermarket";
            int userId = Session.getCurrentUser().getId();
            double reimbursementAmount = calculateReimbursement(invoiceAmount, category);

            String fileUrl = SupabaseUploadService.uploadFile(selectedFile, userId);
            if (fileUrl != null) {
                boolean success = SupabaseUploadService.saveInvoiceToDatabase(
                        userId, fileUrl, category, invoiceAmount, reimbursementAmount
                );
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
            String category = restaurantRadio.isSelected() ? "restaurant" : supermarketRadio.isSelected() ? "supermarket" : "";
            double reimbursement = calculateReimbursement(invoice, category);
            reimbursementAmountLabel.setText(String.format("%.2f €", reimbursement));
        } catch (Exception e) {
            reimbursementAmountLabel.setText("-");
        }
    }

    private double calculateReimbursement(double amount, String category) {
        return switch (category.toLowerCase()) {
            case "restaurant" -> Math.min(amount, 3.0);
            case "supermarket" -> Math.min(amount, 2.5);
            default -> 0.0;
        };
    }

    private void closeWindow() {
        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
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
