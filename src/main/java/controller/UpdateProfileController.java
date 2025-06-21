package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Session;
import model.User;
import model.UserDAO;

public class UpdateProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Felder mit den aktuellen User-Daten füllen
        User u = Session.getCurrentUser();
        nameField.setText(u.getName());
        emailField.setText(u.getEmail());
    }

    /** Speichert Name & E-Mail und schließt das Fenster */
    @FXML
    private void handleSave() {
        String newName  = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        // einfache Validierung
        if (newName.isEmpty() || newEmail.isEmpty()) {
            errorLabel.setText("Name und E-Mail dürfen nicht leer sein.");
            return;
        }
        if (!newEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Bitte eine gültige E-Mail-Adresse eingeben.");
            return;
        }

        int userId = Session.getCurrentUser().getId();
        boolean ok = UserDAO.updateUserProfile(userId, newName, newEmail);
        if (ok) {
            // Session-Objekt aktualisieren
            User u = Session.getCurrentUser();
            u.setName(newName);
            u.setEmail(newEmail);
            // Dialog schließen
            ((Stage)nameField.getScene().getWindow()).close();
        } else {
            errorLabel.setText("Fehler beim Speichern. Bitte erneut versuchen.");
        }
    }

    /** Schliesst das Fenster ohne zu speichern */
    @FXML
    private void handleCancel() {
        ((Stage)nameField.getScene().getWindow()).close();
    }
}
