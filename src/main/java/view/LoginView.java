package view;

import controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {

    private TextField emailField;
    private PasswordField passwordField;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lunchify Login");

        emailField = new TextField();
        passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        statusLabel = new Label();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-padding: 20");

        grid.add(new Label("E-Mail:"), 0, 0);
        grid.add(emailField, 1, 0);

        grid.add(new Label("Passwort:"), 0, 1);
        grid.add(passwordField, 1, 1);

        VBox layout = new VBox(15, grid, loginButton, statusLabel);
        layout.setStyle("-fx-padding: 20");

        loginButton.setOnAction(e -> handleLogin());

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        LoginController loginController = new LoginController();
        boolean success = loginController.login(email, password);

        if (success) {
            statusLabel.setText("Login erfolgreich!");
            // TODO: Scene-Wechsel zum Dashboard
        } else {
            statusLabel.setText("E-Mail oder Passwort falsch.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}