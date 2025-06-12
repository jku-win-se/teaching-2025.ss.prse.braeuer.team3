package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserDashboardController {

    @FXML private BorderPane mainPane;
    @FXML private TilePane starredBox;
    @FXML private TilePane recentlyViewedBox;
    @FXML private Label userNameLabel;
    @FXML private ImageView logoImage;
    @FXML private ToggleButton homeButton, starredButton, requestsButton, settingsButton;
    @FXML private Button notificationButton;

    private javafx.scene.Node homeContent;

    // Thread‐Pool für Hintergrund‐Tasks
    private final Executor executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @FXML
    public void initialize() {
        // 1) Benutzername & Logo sofort setzen
        userNameLabel.setText(Session.getCurrentUser().getName());
        logoImage.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));

        // 2) Home‐Content merken
        homeContent = mainPane.getCenter();

        // 3) Daten für “Starred” und “Recent” asynchron laden
        loadStarredAsync();
        loadRecentAsync();
    }

    /** Lädt “Starred” im Hintergrund und füllt die TilePane, sobald fertig */
    private void loadStarredAsync() {
        int userId = Session.getCurrentUser().getId();
        Task<List<Invoice>> task = new Task<>() {
            @Override
            protected List<Invoice> call() {
                return InvoiceDAO.getStarredInvoicesByUser(userId);
            }
        };

        task.setOnSucceeded(e -> {
            starredBox.getChildren().clear();
            for (Invoice inv : task.getValue()) {
                if (inv.getFileName() != null && !inv.getFileName().isBlank()) {
                    starredBox.getChildren().add(createInvoiceCard(inv));
                }
            }
        });

        task.setOnFailed(e -> {
            // hier ggf. Logging oder Alert
            task.getException().printStackTrace();
        });

        executor.execute(task);
    }

    /** Lädt “Recently Viewed” im Hintergrund und füllt die TilePane */
    private void loadRecentAsync() {
        int userId = Session.getCurrentUser().getId();
        Task<List<Invoice>> task = new Task<>() {
            @Override
            protected List<Invoice> call() {
                return InvoiceDAO.getRecentInvoicesByUser(userId);
            }
        };

        task.setOnSucceeded(e -> {
            recentlyViewedBox.getChildren().clear();
            for (Invoice inv : task.getValue()) {
                if (inv.getFileName() != null && !inv.getFileName().isBlank()) {
                    recentlyViewedBox.getChildren().add(createInvoiceCard(inv));
                }
            }
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });

        executor.execute(task);
    }

    /** Baut pro Invoice eine kleine Vorschau-Kachel */
    private VBox createInvoiceCard(Invoice inv) {
        String trimmed = inv.getFileName().replaceAll("^/+", "");
        String url = "https://onvxredsmjqlufgjjojh.supabase.co"
                + "/storage/v1/object/public/rechnung//" + trimmed;

        // Hintergrund‐Laden aktiviert durch letzten Parameter "true"
        ImageView iv = new ImageView(new Image(url, 100, 0, true, true));
        iv.setPreserveRatio(true);

        VBox box = new VBox(iv);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color:white; "
                + "-fx-padding:5; "
                + "-fx-border-color:#ccc; "
                + "-fx-border-radius:5;");

        // Klick ⇒ Sternchen umschalten
        box.setOnMouseClicked(e -> {
            boolean nowStarred = !inv.isStarred();
            InvoiceDAO.toggleStar(Session.getCurrentUser().getId(), inv.getId(), nowStarred);
            loadStarredAsync();  // nach dem Umschalten neu laden
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
            new Alert(Alert.AlertType.ERROR, "Could not load Login screen.").showAndWait();
        }
    }

    @FXML private void handleAddNew() {
        openModal("/view/AddInvoiceView.fxml", "Upload Invoice");
    }

    /** Gemeinsame Methode zum Öffnen eines modalen Dialogs */
    private void openModal(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage dialog = new Stage();
            dialog.initOwner(mainPane.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Fehler beim Laden der Ansicht:\n" + fxmlPath).showAndWait();
        }
    }
}