import org.mindrot.jbcrypt.BCrypt;

public class generator {
    public static void main(String[] args) {
        String password = "user123";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("BCrypt-Hash: " + hashed);
    }
}

