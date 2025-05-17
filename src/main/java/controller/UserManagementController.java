package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserManagementController {

    @FXML
    private void openAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddUserView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeleteUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteUserView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Delete User");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
