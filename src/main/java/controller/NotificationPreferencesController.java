package controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Session;
import model.UserDAO;

public class NotificationPreferencesController {

    @FXML private CheckBox cbInvoiceEdited;
    @FXML private CheckBox cbInvoiceRejected;
    @FXML private Label lblFeedback;

    @FXML
    public void initialize() {
        int userId = Session.getCurrentUser().getId();

        // Aus DB holen
        cbInvoiceEdited.setSelected(
                UserDAO.getNotificationPref(userId, "INVOICE_EDITED")
        );
        cbInvoiceRejected.setSelected(
                UserDAO.getNotificationPref(userId, "INVOICE_REJECTED")
        );

        lblFeedback.setOpacity(0);  // Feedback-Label initial unsichtbar
    }

    @FXML
    private void onToggleInvoiceEdited() {
        saveAndFeedback("INVOICE_EDITED", cbInvoiceEdited.isSelected());
    }

    @FXML
    private void onToggleInvoiceRejected() {
        saveAndFeedback("INVOICE_REJECTED", cbInvoiceRejected.isSelected());
    }

    private void saveAndFeedback(String type, boolean enabled) {
        int userId = Session.getCurrentUser().getId();
        boolean ok = UserDAO.updateNotificationPref(userId, type, enabled);
        if (ok) {
            // auch im Session-Objekt aktualisieren
            Session.getCurrentUser().setNotificationPref(type, enabled);
            showFeedback();
        } else {
            // hier ggfs. Fehlermeldung integrieren
        }
    }

    private void showFeedback() {
        lblFeedback.setOpacity(1);
        FadeTransition ft = new FadeTransition(Duration.seconds(2), lblFeedback);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
    }

    /** Schließt das Modal */
    @FXML
    private void onClose() {
        // Label oder Button referenzieren, um Stage zu holen
        Stage stage = (Stage) lblFeedback.getScene().getWindow();
        stage.close();
    }
}
