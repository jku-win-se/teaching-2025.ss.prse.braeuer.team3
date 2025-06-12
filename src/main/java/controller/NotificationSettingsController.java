package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import model.NotificationSettings;
import model.NotificationSettingsDAO;
import model.Session;

public class NotificationSettingsController {

    @FXML private ToggleButton approvedToggle;
    @FXML private ToggleButton rejectedToggle;
    @FXML private ToggleButton monthlyToggle;

    private NotificationSettings settings;

    @FXML
    public void initialize() {
        int userId = Session.getCurrentUser().getId();
        settings = NotificationSettingsDAO.getSettingsForUser(userId);

        approvedToggle.setSelected(settings.isNotifyInvoiceApproved());
        rejectedToggle.setSelected(settings.isNotifyInvoiceRejected());
        monthlyToggle.setSelected(settings.isNotifyMonthlySummary());

        approvedToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean ok = NotificationSettingsDAO.updateInvoiceApprovedFlag(userId, newVal);
            if (!ok) {
                showError("Could not update 'Invoice Approved' preference.");
                approvedToggle.setSelected(oldVal);
            }
        });

        rejectedToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean ok = NotificationSettingsDAO.updateInvoiceRejectedFlag(userId, newVal);
            if (!ok) {
                showError("Could not update 'Invoice Rejected' preference.");
                rejectedToggle.setSelected(oldVal);
            }
        });

        monthlyToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean ok = NotificationSettingsDAO.updateMonthlySummaryFlag(userId, newVal);
            if (!ok) {
                showError("Could not update 'Monthly Summary' preference.");
                monthlyToggle.setSelected(oldVal);
            }
        });
    }

    @FXML
    private void handleClose() {
        // Kurze Bestätigung anzeigen:
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Your notification preferences have been updated.");
        a.setHeaderText(null);
        a.showAndWait();

        // Schließe das Modal
        ((Stage) approvedToggle.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
