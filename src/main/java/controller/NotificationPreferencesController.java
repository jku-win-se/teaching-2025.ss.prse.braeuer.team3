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

    @FXML private CheckBox cbInvoiceApproved;
    @FXML private CheckBox cbInvoiceRejected;
    @FXML private CheckBox cbMonthlySummary;
    @FXML private Label lblFeedback;

    @FXML
    public void initialize() {
        int userId = Session.getCurrentUser().getId();
        // Aktuelle Präferenzen aus der DB holen
        cbInvoiceApproved.setSelected(
                UserDAO.getNotificationPref(userId, "INVOICE_APPROVED")
        );
        cbInvoiceRejected.setSelected(
                UserDAO.getNotificationPref(userId, "INVOICE_REJECTED")
        );
        cbMonthlySummary.setSelected(
                UserDAO.getNotificationPref(userId, "MONTHLY_SUMMARY")
        );
    }

    /** Wird ausgelöst, wenn InvoiceApproved‐Checkbox getoggelt wird */
    @FXML
    private void onToggleInvoiceApproved() {
        saveAndFeedback("INVOICE_APPROVED", cbInvoiceApproved.isSelected());
    }

    /** Wird ausgelöst, wenn InvoiceRejected‐Checkbox getoggelt wird */
    @FXML
    private void onToggleInvoiceRejected() {
        saveAndFeedback("INVOICE_REJECTED", cbInvoiceRejected.isSelected());
    }

    /** Wird ausgelöst, wenn MonthlySummary‐Checkbox getoggelt wird */
    @FXML
    private void onToggleMonthlySummary() {
        saveAndFeedback("MONTHLY_SUMMARY", cbMonthlySummary.isSelected());
    }

    /**
     * Speichert die Präferenz in der DB und zeigt ein kurzes Feedback.
     */
    private void saveAndFeedback(String type, boolean enabled) {
        int userId = Session.getCurrentUser().getId();
        boolean ok = UserDAO.updateNotificationPref(userId, type, enabled);

        if (ok) {
            // Feedback einblenden
            showFeedback();

            // Optional: auch ins Session‐Objekt schreiben, falls gebraucht
            Session.getCurrentUser().setNotificationPref(type, enabled);
        } else {
            lblFeedback.setText("Fehler beim Speichern.");
            showFeedback();
        }
    }

    /**
     * Blendet das Feedback‐Label ein und wieder aus.
     */
    private void showFeedback() {
        lblFeedback.setText("Einstellungen aktualisiert.");
        lblFeedback.setOpacity(1);
        FadeTransition ft = new FadeTransition(Duration.seconds(2), lblFeedback);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
    }

    /**
     * Optional: Methode, um den Dialog zu schließen,
     * z.B. wenn du einen „Schließen“-Button anbietest.
     */
    @FXML
    private void onClose() {
        Stage stage = (Stage) lblFeedback.getScene().getWindow();
        stage.close();
    }
}
