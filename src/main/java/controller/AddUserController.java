package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;

public class AddUserController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().clear();
        roleChoiceBox.getItems().addAll("admin", "user");
        roleChoiceBox.setValue("user");
    }

    @FXML
    private void handleAddUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleChoiceBox.getValue();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Name and email cannot be empty.");
            return;
        }

        if (UserDAO.emailExists(email)) {
            showAlert("Email already exists.");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRolle(role);

        boolean success = UserDAO.addUser(name, email, role);
        if (success) {
            showAlert("User successfully added.");
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Failed to add user.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add User");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}