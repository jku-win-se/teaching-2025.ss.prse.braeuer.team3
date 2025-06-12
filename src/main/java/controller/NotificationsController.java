package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Notification;
import model.NotificationDAO;
import model.Session;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsController {

    @FXML private ListView<Notification> listView;
    @FXML private Button onClose;

    @FXML
    public void initialize() {
        int userId = Session.getCurrentUser().getId();
        List<Notification> notifs = NotificationDAO.getNotificationsForUser(userId);

        listView.getItems().setAll(notifs);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String formattedDate = item.getTimestamp()
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    setText(formattedDate + "  –  " + item.getMessage());
                }
            }
        });
    }

    /** Close-Button */
    @FXML
    private void onClose() {
        Stage dlg = (Stage) onClose.getScene().getWindow();
        dlg.close();
    }
}