package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import model.Session;
import model.User;

import java.io.IOException;

public class UserDashboardController {

    @FXML private TilePane starredBox;
    @FXML private TilePane recentlyViewedBox;
    @FXML private Button logoutButton;
    @FXML private Label userNameText;
    @FXML private Button addNewButton;

    @FXML
    public void initialize() {
        loadUserInfo();
        loadPreviews();
    }

    private void loadUserInfo() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            userNameText.setText(currentUser.getName());
        } else {
            userNameText.setText("Unknown User");
        }
    }

    private void loadPreviews() {
        Image starImage = new Image(getClass().getResourceAsStream("/assets/starred.png"));
        Image eyeImage = new Image(getClass().getResourceAsStream("/assets/recent.png"));

        for (int i = 0; i < 3; i++) {
            ImageView starView = new ImageView(starImage);
            starView.setFitWidth(180);
            starView.setFitHeight(120);
            starView.getStyleClass().add("image-preview");
            starredBox.getChildren().add(starView);
        }

        for (int i = 0; i < 3; i++) {
            ImageView eyeView = new ImageView(eyeImage);
            eyeView.setFitWidth(180);
            eyeView.setFitHeight(120);
            eyeView.getStyleClass().add("image-preview");
            recentlyViewedBox.getChildren().add(eyeView);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lunchify Login");
            stage.setMaximized(false);
            stage.show();

            Session.clear();
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
            popupStage.setTitle("Add Invoice");
            popupStage.setScene(new Scene(root));
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}