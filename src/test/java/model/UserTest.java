package model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testAddUser() {
        // Arrange
        User testUser = new User();
        testUser.setEmail("testuser@lunchify.at");
        testUser.setName("JUnit Test User");
        testUser.setRolle("user");

        // Act
        boolean added = UserDAO.addUser(testUser);

        // Assert
        assertTrue(added, "The user should be added successfully");
        assertTrue(UserDAO.emailExists("testuser@lunchify.at"), "The added user's email should exist in DB");

        // Clean up
        UserDAO.deleteUserByEmail("testuser@lunchify.at");
    }

    @Test
    void testDeleteUser() {
        // Arrange – prvo dodaj korisnika kog ćeš obrisati
        User deleteUser = new User();
        deleteUser.setEmail("deletetest@lunchify.at");
        deleteUser.setName("ToDelete");
        deleteUser.setRolle("user");

        UserDAO.addUser(deleteUser);
        assertTrue(UserDAO.emailExists("deletetest@lunchify.at"), "User must exist before deleting");

        // Act
        boolean deleted = UserDAO.deleteUserByEmail("deletetest@lunchify.at");

        // Assert
        assertTrue(deleted, "The user should be deleted successfully");
        assertFalse(UserDAO.emailExists("deletetest@lunchify.at"), "User should not exist after deletion");
    }
}
