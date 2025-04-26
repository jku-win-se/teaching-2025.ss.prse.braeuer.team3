package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class AddInvoiceController {

    @FXML private Button uploadButton;
    @FXML private Button submitButton;
    @FXML private Label fileNameLabel;
    @FXML private Label errorLabel;
    @FXML private RadioButton restaurantRadio;
    @FXML private RadioButton supermarketRadio;
    @FXML private TextField amountField;

    private File selectedFile;
    private ToggleGroup group;

    @FXML
    public void initialize() {
        group = new ToggleGroup();
        restaurantRadio.setToggleGroup(group);
        supermarketRadio.setToggleGroup(group);
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleUploadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Invoice File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image and PDF Files", "*.jpg", "*.jpeg", "*.png", "*.pdf")
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
    private void handleSubmitButton() {
        // Hier nur Platzhalter für spätere Funktionalität
        if (selectedFile == null) {
            showError("Please choose a file first.");
            return;
        }

        if (group.getSelectedToggle() == null) {
            showError("Please select invoice type.");
            return;
        }

        if (amountField.getText().trim().isEmpty()) {
            showError("Please enter the total amount.");
            return;
        }

        // Success-Placeholder
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Submission Successful");
        alert.setHeaderText(null);
        alert.setContentText("Your invoice was submitted successfully!");
        alert.showAndWait();

        // Fenster schließen
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
