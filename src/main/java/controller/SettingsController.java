package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML private Button changePasswordButton;
    @FXML private Button updateProfileButton;
    @FXML private Button notificationPrefsButton;

    /** Öffnet das Change-Password-Modal */
    @FXML
    private void handleChangePassword() {
        openModal("/view/ChangePasswordView.fxml", "Change Password");
    }

    /** Öffnet das Update-Profile-Modal */
    @FXML
    private void handleUpdateProfile() {
        openModal("/view/UpdateProfileView.fxml", "Update Profile");
    }

    /** Öffnet das Notification-Preferences-Modal */
    @FXML
    private void handleNotificationPreferences() {
        openModal("/view/NotificationPreferencesView.fxml", "Notification Preferences");
    }

    /**
     * Hilfsmethode, um ein FXML in einem modal Dialog zu öffnen
     */
    private void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}