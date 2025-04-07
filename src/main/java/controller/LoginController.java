package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;
import model.Session;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private ImageView logoImage;

    @FXML
    private ProgressIndicator loadingSpinner;

    private int failedAttempts = 0;

    @FXML
    public void initialize() {
        Image logo = new Image(getClass().getResourceAsStream("/assets/logo.png"));
        logoImage.setImage(logo);

        loadingSpinner.setVisible(false);

        loginButton.setOnAction(e -> handleLogin());

        forgotPasswordLink.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reset password");
            dialog.setHeaderText("Forgot your password?");
            dialog.setContentText("Please enter your registered e-mail address:");

            dialog.showAndWait().ifPresent(email -> {
                if (email.trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "E-mail must not be empty.");
                } else if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    showAlert(Alert.AlertType.WARNING, "Please enter a valid e-mail address.");
                } else {
                    boolean userExists = UserDAO.emailExists(email);
                    if (userExists) {
                        showAlert(Alert.AlertType.INFORMATION, "If this e-mail is registered, we will send you further information.");
                        // TODO: implement password reset or email sending
                    } else {
                        showAlert(Alert.AlertType.WARNING, "This e-mail address is not registered.");
                    }
                }
            });
        });
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.WARNING, "Please enter a valid e-mail address.");
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
            return;
        }

        loadingSpinner.setVisible(true);
        loginButton.setDisable(true);

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return UserDAO.validateLogin(email, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            User user = loginTask.getValue();
            loadingSpinner.setVisible(false);
            loginButton.setDisable(false);

            if (user != null) {
                failedAttempts = 0;
                Session.setCurrentUser(user); // << Speichert eingeloggten User global

                String role = user.getRolle();
                System.out.println("Login successfully as: " + role);

                try {
                    String fxmlPath;
                    String windowTitle;

                    if ("admin".equalsIgnoreCase(role)) {
                        fxmlPath = "/view/AdminDashboardView.fxml";
                        windowTitle = "Admin Dashboard";
                    } else {
                        fxmlPath = "/view/UserDashboardView.fxml";
                        windowTitle = "Dashboard";
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle(windowTitle);
                    stage.setMaximized(true); // Maximiert Fenster
                    stage.show();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error loading the next view.");
                }

            } else {
                failedAttempts++;
                showAlert(Alert.AlertType.ERROR, "Login failed! E-mail or password is incorrect.");

                if (failedAttempts >= 5) {
                    showAlert(Alert.AlertType.WARNING, "Too many failed attempts. Please wait 30 seconds.");
                    loginButton.setDisable(true);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                loginButton.setDisable(false);
                                failedAttempts = 0;
                            });
                        }
                    }, 30_000);
                }
            }
        });

        loginTask.setOnFailed(event -> {
            loadingSpinner.setVisible(false);
            loginButton.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "An unexpected error has occurred.");
            loginTask.getException().printStackTrace();
        });

        new Thread(loginTask).start();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean login(String email, String password) {
        User user = UserDAO.validateLogin(email, password);
        return user != null;
    }
}
