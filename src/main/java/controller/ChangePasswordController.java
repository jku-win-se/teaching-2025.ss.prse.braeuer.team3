package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Session;
import model.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordController {

    @FXML private PasswordField newPwd;
    @FXML private PasswordField confirmPwd;
    @FXML private Label errorLabel;

    @FXML
    private void handleChange() {
        String p1 = newPwd.getText();
        String p2 = confirmPwd.getText();
        if (p1.isEmpty() || !p1.equals(p2)) {
            errorLabel.setText("Passwords must match and not be empty.");
            return;
        }

        String newHash = BCrypt.hashpw(p1, BCrypt.gensalt());
        boolean ok = UserDAO.updatePasswordAndClearFlag(
                Session.getCurrentUser().getId(), newHash
        );
        if (ok) {
            // Session-User updaten
            Session.getCurrentUser().setPasswordHash(newHash);
            Session.getCurrentUser().setMustChangePassword(false);
            // Dialog schließen
            ((Stage)newPwd.getScene().getWindow()).close();
        } else {
            errorLabel.setText("Error updating password. Try again.");
        }
    }
}
