package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Invoice;
import model.InvoiceDAO;
import model.Session;

import java.io.IOException;
import java.util.List;

public class UserDashboardController {

    @FXML private BorderPane mainPane;
    @FXML private TilePane starredBox;
    @FXML private TilePane recentlyViewedBox;
    @FXML private Label userNameLabel;
    @FXML private ImageView logoImage;
    @FXML private ToggleButton homeButton, starredButton, requestsButton, settingsButton;
    @FXML private Button notificationButton;

    private javafx.scene.Node homeContent;

    @FXML
    public void initialize() {
        // Benutzername & Logo
        userNameLabel.setText(Session.getCurrentUser().getName());
        logoImage.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));

        // Home-Content merken
        homeContent = mainPane.getCenter();

        // Starred & Recent laden
        loadStarredPreviews();
        loadRecentlyViewedPreviews();
    }

    private void loadStarredPreviews() {
        starredBox.getChildren().clear();
        int userId = Session.getCurrentUser().getId();
        List<Invoice> starred = InvoiceDAO.getStarredInvoicesByUser(userId);
        for (Invoice inv : starred) {
            if (inv.getFileName() != null && !inv.getFileName().isBlank()) {
                starredBox.getChildren().add(createInvoiceCard(inv));
            }
        }
    }

    private void loadRecentlyViewedPreviews() {
        recentlyViewedBox.getChildren().clear();
        int userId = Session.getCurrentUser().getId();
        List<Invoice> recent = InvoiceDAO.getRecentInvoicesByUser(userId);
        for (Invoice inv : recent) {
            if (inv.getFileName() != null && !inv.getFileName().isBlank()) {
                recentlyViewedBox.getChildren().add(createInvoiceCard(inv));
            }
        }
    }

    private VBox createInvoiceCard(Invoice inv) {
        String trimmed = inv.getFileName().replaceAll("^/+", "");
        String url = "https://onvxredsmjqlufgjjojh.supabase.co/storage/v1/object/public/rechnung//" + trimmed;
        ImageView iv = new ImageView(new Image(url, 100, 0, true, true));
        iv.setPreserveRatio(true);

        VBox box = new VBox(iv);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color:white; -fx-padding:5; -fx-border-color:#ccc; -fx-border-radius:5;");
        box.setOnMouseClicked(e -> {
            boolean nowStarred = !inv.isStarred();
            InvoiceDAO.toggleStar(Session.getCurrentUser().getId(), inv.getId(), nowStarred);
            loadStarredPreviews();
        });
        return box;
    }

    @FXML private void handleHomeClick() throws IOException {
        mainPane.setCenter(homeContent);
    }

    @FXML private void handleStarredClick() throws IOException {
        mainPane.setCenter(FXMLLoader.load(getClass().getResource("/view/StarredView.fxml")));
    }

    @FXML private void handleRequestsClick() throws IOException {
        mainPane.setCenter(FXMLLoader.load(getClass().getResource("/view/RequestHistoryView.fxml")));
    }

    @FXML private void handleSettingsClick() throws IOException {
        mainPane.setCenter(FXMLLoader.load(getClass().getResource("/view/SettingsView.fxml")));
    }
    @FXML private void handleNotificationsClick() {
        openModal("/view/NotificationsView.fxml", "Your Notifications");
    }


    @FXML private void handleLogout() {
        try {
            Session.clearCurrentUser();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            loginScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Could not load Login screen."
            ).showAndWait();
        }
    }

    @FXML private void handleAddNew() {
        openModal("/view/AddInvoiceView.fxml", "Upload Invoice");
    }

    /** Hilfsmethode zum Öffnen eines modalen Dialogs mit dem angegebenen FXML */
    private void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initOwner(mainPane.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Fehler beim Laden der Ansicht:\n" + fxmlPath
            ).showAndWait();
        }
    }
}