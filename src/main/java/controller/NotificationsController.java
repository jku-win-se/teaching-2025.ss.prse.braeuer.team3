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
    @FXML private Button closeButton;  // muss mit fx:id im FXML übereinstimmen

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

    /** Schliesst das Modal-Fenster */
    @FXML
    private void onClose() {
        Stage dialog = (Stage) closeButton.getScene().getWindow();
        dialog.close();
    }
}