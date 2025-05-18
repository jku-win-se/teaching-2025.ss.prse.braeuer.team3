package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Session;

import java.io.IOException;

public class UserDashboardController {

    @FXML private BorderPane mainPane;
    @FXML private TilePane starredBox;
    @FXML private TilePane recentlyViewedBox;
    @FXML private Label userNameLabel;
    @FXML private ImageView logoImage;

    // Referenz auf den ursprünglichen Home-Content für "Home"-Navigation
    private Node homeContent;

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() == null) {
            try {
                Stage stage = (Stage) mainPane.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Benutzername und Logo setzen
        userNameLabel.setText(Session.getCurrentUser().getName());
        logoImage.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));

        // Starred und Recently loaded
        loadStarredPreviews();
        loadRecentlyViewedPreviews();

        // Home-Content merken (Initiales Center-Node)
        homeContent = mainPane.getCenter();
    }

    private void loadStarredPreviews() {
        starredBox.getChildren().clear();
        starredBox.getChildren().add(createCard("star_card.png"));
    }

    private void loadRecentlyViewedPreviews() {
        recentlyViewedBox.getChildren().clear();
        recentlyViewedBox.getChildren().add(createCard("eye_card.png"));
    }

    private javafx.scene.layout.VBox createCard(String imageName) {
        ImageView previewImage = new ImageView();
        try {
            previewImage.setImage(new Image(
                    getClass().getResourceAsStream("/images/" + imageName),
                    150, 150, true, true));
        } catch (Exception e) {
            System.out.println("Image not found: " + imageName);
        }
        previewImage.setPreserveRatio(true);

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(previewImage);
        box.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10;");
        box.setPrefSize(160, 160);
        box.setMaxSize(160, 160);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        return box;
    }

    @FXML
    private void handleHomeClick() {
        // Zurück zum ursprünglichen Home-Content
        mainPane.setCenter(homeContent);
    }

    @FXML
    private void handleStarredClick() {
        switchContent("/view/StarredView.fxml");
    }

    @FXML
    private void handleRequestsClick() {
        switchContent("/view/RequestHistoryView.fxml");
    }

    @FXML
    private void handleSettingsClick() {
        switchContent("/view/SettingsView.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clearCurrentUser();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNew() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AddInvoiceView.fxml"));
            Stage popup = new Stage();
            popup.initOwner(mainPane.getScene().getWindow());
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Upload Invoice");
            popup.setResizable(false);
            popup.setScene(new Scene(root));
            popup.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}