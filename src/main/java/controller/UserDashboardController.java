package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Session;

import java.io.IOException;

public class UserDashboardController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private TilePane starredBox;

    @FXML
    private TilePane recentlyViewedBox;

    @FXML
    private Label userNameLabel;

    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() == null) {
            try {
                Stage stage = (Stage) mainPane.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Wenn eingeloggt: Benutzername anzeigen
        userNameLabel.setText(Session.getCurrentUser().getName());
        if (logoImage != null) {
            logoImage.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));
        }
        // Starred und Recently Viewed Previews laden
        loadStarredPreviews();
        loadRecentlyViewedPreviews();
    }


    private void loadStarredPreviews() {
        starredBox.getChildren().clear();
        starredBox.getChildren().add(createCard("star_card.png"));
    }

    private void loadRecentlyViewedPreviews() {
        recentlyViewedBox.getChildren().clear();
        recentlyViewedBox.getChildren().add(createCard("eye_card.png"));
    }

    private VBox createCard(String imageName) {
        ImageView previewImage = new ImageView();
        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/images/" + imageName),
                    150, 150,
                    true,
                    true
            );
            previewImage.setImage(image);
        } catch (Exception e) {
            System.out.println("Image not found: /images/" + imageName);
        }
        previewImage.setPreserveRatio(true);

        VBox box = new VBox(previewImage);
        box.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10;");
        box.setPrefSize(160, 160);
        box.setMaxSize(160, 160);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        return box;
    }


    // Methoden für Sidebar-Buttons

    @FXML
    private void handleHomeClick() {
        starredBox.setVisible(true);
        recentlyViewedBox.setVisible(true);
        mainPane.setCenter(null);
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAddNew() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddInvoiceView.fxml"));
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.setTitle("Upload Invoice");
                popupStage.setScene(new Scene(root));
                popupStage.setResizable(false);
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        private void switchContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}