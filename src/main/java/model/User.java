package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private int id;
    private StringProperty email = new SimpleStringProperty();
    private String        password;
    private StringProperty name  = new SimpleStringProperty();
    private StringProperty rolle = new SimpleStringProperty();
    private BooleanProperty mustChangePassword = new SimpleBooleanProperty(false);

    public User(int id, String email, String password, String name, String rolle, boolean mustChange) {
        this.id    = id;
        this.email.set(email);
        this.password = password;
        this.name .set(name);
        this.rolle.set(rolle);
        this.mustChangePassword.set(mustChange);
    }

    public User() {
        // leerer Konstruktor für FXML etc.
    }

    // --- Getter ---

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email.get();
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name.get();
    }

    public String getRolle() {
        return rolle.get();
    }

    /** Gibt zurück, ob der User sein Passwort beim nächsten Login ändern muss */
    public boolean isMustChangePassword() {
        return mustChangePassword.get();
    }

    // --- Setter ---

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setRolle(String rolle) {
        this.rolle.set(rolle);
    }

    /** Setzt das Flag, ob der User sein Passwort ändern muss */
    public void setMustChangePassword(boolean flag) {
        this.mustChangePassword.set(flag);
    }

    // --- Property-Methoden ---

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty rolleProperty() {
        return rolle;
    }

    public BooleanProperty mustChangePasswordProperty() {
        return mustChangePassword;
    }

    /** Alias, falls irgendwo setPasswordHash(...) aufgerufen wird */
    public void setPasswordHash(String hash) {
        setPassword(hash);
    }
}