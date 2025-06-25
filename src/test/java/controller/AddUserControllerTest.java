package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddUserControllerTest {

    private AddUserController controller;

    @BeforeEach
    void setUp() {
        controller = new AddUserController();
    }

    @Test
    void testControllerInitialization() {
        assertNotNull(controller);
    }
}
