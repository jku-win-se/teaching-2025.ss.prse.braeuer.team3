// UserManagementController.java
package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;

import java.io.IOException;
import java.util.List;

public class UserManagementController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private Button deleteUserButton;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().rolleProperty());

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = UserDAO.getAllUsers();
        userList.setAll(users);
        userTable.setItems(userList);
    }

    @FXML
    private void openAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddUserView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean deleted = UserDAO.deleteUserByEmail(selected.getEmail());
            if (deleted) {
                userList.remove(selected);
            } else {
                showAlert("Could not delete user.");
            }
        } else {
            showAlert("Please select a user.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Management");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
