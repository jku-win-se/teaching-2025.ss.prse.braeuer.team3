package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Admin dashboard view with filtering and export functionality
 */
public class AdminDashboardView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for admin dashboard
        Parent root = FXMLLoader.load(getClass().getResource("/view/AdminDashboard.fxml"));

        // Configure the stage
        primaryStage.setTitle("Admin Dashboard - Invoice Management");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Launches the admin dashboard (call this after successful login)
     */
    public static void showDashboard() {
        launch();
    }
}