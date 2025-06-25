package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class LoginControllerTest {
    @Test
    void login_directCall_returnsFalseForUnknownUser() {
        LoginController controller = new LoginController();
        boolean result = controller.login("notexist@test.com", "invalid");
        assertFalse(result);
    }


    @Test
    void emailValidation_fails_forInvalidEmail() {
        String invalidEmail = "invalid@";
        boolean isValid = invalidEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        assertFalse(isValid, "Invalid e-mail ");
    }

    @Test
    void emailValidation_passes_forValidEmail() {
        String validEmail = "user@example.com";
        boolean isValid = validEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        assertTrue(isValid, "Eine gültige E-Mail muss die Validierung bestehen..");
    }
}
