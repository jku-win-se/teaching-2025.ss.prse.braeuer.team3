package at.jku.se.lunchify.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class LoginControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Lade die korrekte FXML-Datei
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testLoginPageIsAccessible() {
        verifyThat("#emailField", isVisible());
        verifyThat("#passwordField", isVisible());
        verifyThat("#loginButton", isVisible());
    }
}
