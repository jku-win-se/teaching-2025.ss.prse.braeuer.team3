package model;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String rolle; // 👈 neues Feld für Benutzerrolle

    public User(int id, String email, String password, String name, String rolle) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.rolle = rolle;
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRolle() {
        return rolle;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRolle(String rolle) {
        this.rolle = rolle;
    }
}