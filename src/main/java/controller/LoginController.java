package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Session;
import model.User;
import model.UserDAO;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private ImageView logoImage;
    @FXML private ProgressIndicator loadingSpinner;

    private int failedAttempts = 0;

    @FXML
    public void initialize() {
        // Logo laden
        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
        logoImage.setImage(logo);

        loadingSpinner.setVisible(false);
        loginButton.setOnAction(e -> handleLogin());
        setupForgotPasswordLink();
    }

    private void setupForgotPasswordLink() {
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
                        showAlert(Alert.AlertType.INFORMATION,
                                "If this e-mail is registered, you will receive reset instructions.");
                        // TODO: implement actual email sending
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

        loginTask.setOnSucceeded(evt -> {
            loadingSpinner.setVisible(false);
            loginButton.setDisable(false);

            User user = loginTask.getValue();
            if (user != null) {
                failedAttempts = 0;
                Session.setCurrentUser(user);
                System.out.println("Login successfully as: " + user.getRolle());

                // Erst Passwort ändern, wenn Flag gesetzt
                if (user.isMustChangePassword()) {
                    try {
                        showChangePasswordDialog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error opening password-change dialog.");
                        return;
                    }
                }

                openDashboard(user);

            } else {
                failedAttempts++;
                showAlert(Alert.AlertType.ERROR, "Login failed! E-mail or password is incorrect.");

                if (failedAttempts >= 5) {
                    showAlert(Alert.AlertType.WARNING,
                            "Too many failed attempts. Please wait 30 seconds.");
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

        loginTask.setOnFailed(evt -> {
            loadingSpinner.setVisible(false);
            loginButton.setDisable(false);
            showAlert(Alert.AlertType.ERROR, "An unexpected error has occurred.");
            loginTask.getException().printStackTrace();
        });

        new Thread(loginTask).start();
    }

    private void showChangePasswordDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/ChangePasswordView.fxml")
        );
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.initOwner(loginButton.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Change Your Password");
        dialog.setResizable(false);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private void openDashboard(User user) {
        try {
            String fxml, title;
            if ("admin".equalsIgnoreCase(user.getRolle())) {
                fxml = "/view/AdminDashboardView.fxml";
                title = "Admin Dashboard";
            } else {
                fxml = "/view/UserDashboardView.fxml";
                title = "Dashboard";
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading the next view.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setTitle("Login");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /** ➤ Fügt die fehlende login(...)–Methode wieder hinzu */
    public boolean login(String email, String password) {
        return UserDAO.validateLogin(email, password) != null;
    }
}