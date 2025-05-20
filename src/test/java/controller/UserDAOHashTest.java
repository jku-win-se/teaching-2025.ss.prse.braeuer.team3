package controller;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOHashTest {

    @Test
    void defaultPasswordIsHashed() {
        String raw = "default123";
        String hash = BCrypt.hashpw(raw, BCrypt.gensalt());
        assertNotEquals(raw, hash);
        assertTrue(BCrypt.checkpw(raw, hash));
    }
}
