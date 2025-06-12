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

    // ----- Neue Properties für Notification-Preferences -----
    private BooleanProperty invoiceApprovedPref = new SimpleBooleanProperty(true);
    private BooleanProperty invoiceRejectedPref = new SimpleBooleanProperty(true);
    private BooleanProperty monthlySummaryPref  = new SimpleBooleanProperty(false);

    /** Vollständiger Konstruktor */
    public User(int id,
                String email,
                String password,
                String name,
                String rolle,
                boolean mustChange) {
        this.id    = id;
        this.email.set(email);
        this.password = password;
        this.name .set(name);
        this.rolle.set(rolle);
        this.mustChangePassword.set(mustChange);
        // Notification-Defaults bleiben hier auf true/true/false
    }

    /** Leer-Konstruktor für FXML etc. */
    public User() { }

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

    /** Muss das Passwort ändern? */
    public boolean isMustChangePassword() {
        return mustChangePassword.get();
    }

    /** Zum Auslesen einer Notification-Einstellung nach Typ */
    public boolean getNotificationPref(String type) {
        return switch (type) {
            case "INVOICE_APPROVED"  -> invoiceApprovedPref.get();
            case "INVOICE_REJECTED"  -> invoiceRejectedPref.get();
            case "MONTHLY_SUMMARY"   -> monthlySummaryPref.get();
            default                  -> false;
        };
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

    /** Setzt das Flag für Erst-Passwort-Änderung */
    public void setMustChangePassword(boolean flag) {
        this.mustChangePassword.set(flag);
    }

    /** Setzt eine Notification-Einstellung nach Typ */
    public void setNotificationPref(String type, boolean enabled) {
        switch (type) {
            case "INVOICE_APPROVED"  -> invoiceApprovedPref.set(enabled);
            case "INVOICE_REJECTED"  -> invoiceRejectedPref.set(enabled);
            case "MONTHLY_SUMMARY"   -> monthlySummaryPref.set(enabled);
        }
    }

    /** Alias, falls irgendwo setPasswordHash(...) aufgerufen wird */
    public void setPasswordHash(String hash) {
        setPassword(hash);
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

    public BooleanProperty invoiceApprovedPrefProperty() {
        return invoiceApprovedPref;
    }

    public BooleanProperty invoiceRejectedPrefProperty() {
        return invoiceRejectedPref;
    }

    public BooleanProperty monthlySummaryPrefProperty() {
        return monthlySummaryPref;
    }
}