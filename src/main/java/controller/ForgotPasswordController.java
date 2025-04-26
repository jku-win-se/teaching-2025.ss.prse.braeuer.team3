package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Button resetButton;

    @FXML
    private Label infoLabel;

    @FXML
    public void initialize() {
        infoLabel.setVisible(false);

        resetButton.setOnAction(event -> handleReset());
    }

    private void handleReset() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            infoLabel.setText("Please enter your email address.");
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setVisible(true);
        } else {
            infoLabel.setText("If an account exists, a reset email was sent.");
            infoLabel.setStyle("-fx-text-fill: green;");
            infoLabel.setVisible(true);

        }
    }
}
