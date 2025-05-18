package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private int id;
    private StringProperty email;
    private String password;
    private StringProperty name;
    private StringProperty rolle; // 👈 neues Feld für Benutzerrolle

    public User(int id, String email, String password, String name, String rolle) {
        this.id = id;
        this.email = new SimpleStringProperty(email);
        this.password = password;
        this.name = new SimpleStringProperty(name);
        this.rolle = new SimpleStringProperty(rolle);
    }

    public User() {
        this.name = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.rolle = new SimpleStringProperty();
    }

    // Getter
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

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }
    public StringProperty emailProperty() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return name;
    }


    public void setRolle(String rolle) { this.rolle.set(rolle);}
    public StringProperty rolleProperty() {
        return rolle;
    }
}