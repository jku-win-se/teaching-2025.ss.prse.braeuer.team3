#UML-Diagramm


![UML_prse](https://github.com/user-attachments/assets/368fc4b0-d02f-4a65-980c-f76ed93e2c02)


# Lunchify – Projektdokumentation (v1)

## 1. Einleitung

Lunchify ist eine JavaFX-Anwendung zur Einreichung, Verwaltung und Rückerstattung von Essensausgaben für Mitarbeiter:innen. Das System unterstützt das Hochladen von Rechnungen (PDF, JPG, PNG), eine automatische Erstattungsberechnung sowie eine rollenbasierte Verwaltung mit Benutzer:innen- und Admin-Dashboard.

---

## 2. Systemarchitektur

### 2.1. Views (JavaFX FXML)

| View | Beschreibung |
|------|--------------|
| `LoginView.fxml` | Login-Screen mit E-Mail & Passwort |
| `ForgotPasswordView.fxml` | Passwort-Wiederherstellung |
| `UserDashboardView.fxml` | Dashboard für eingeloggte Nutzer:innen |
| `AddInvoiceView.fxml` | Popup zum Hochladen einer Rechnung |
| `RequestHistoryView.fxml` | Übersicht hochgeladener Rechnungen |
| `StarredView.fxml` | Favoritenansicht für markierte Anträge |
| `SettingsView.fxml` | App-Einstellungen (Sprache, UI etc.) |
| `AdminDashboardView.fxml` | Admin-Panel zur Einsicht und Verwaltung aller Rechnungen |

---

### 2.2. Datenbankmodell (PostgreSQL via Supabase)

#### Tabelle `benutzer`

| Spalte      | Typ    | Beschreibung                     |
|-------------|--------|----------------------------------|
| `id`        | int    | Primärschlüssel                  |
| `email`     | text   | Login-E-Mail                     |
| `passwort`  | text   | Passwort-Hash (BCrypt)           |
| `rolle`     | enum   | `USER`, `ADMIN`                  |

#### Tabelle `rechnung`

| Spalte        | Typ     | Beschreibung                           |
|---------------|----------|----------------------------------------|
| `id`          | int      | Primärschlüssel                        |
| `user_id`     | int      | Fremdschlüssel auf `benutzer.id`       |
| `type`        | enum     | `RESTAURANT`, `SUPERMARKET`           |
| `amount`      | decimal  | Betrag in EUR                         |
| `status`      | enum     | `SUBMITTED`, `APPROVED`, `REJECTED`   |
| `upload_date` | date     | Upload-Datum                          |
| `file_url`    | text     | URL zum Supabase Storage              |

---

## 3. Funktionen & Features

-  **Login/Logout** mit Passwort-Hashing (BCrypt)
-  **Rechnung hochladen** inkl. Datei-Upload und Kategorisierung
-  **Automatische Erstattungsberechnung**
-  **Diagrammübersicht** (z. B. Verteilung Restaurant/Supermarkt)
- ✏ **Bearbeiten und Löschen** (nur im Status `SUBMITTED`)
-  **Favoriten markieren** und anzeigen
-  **Einstellungen** für User
-  **Admin-Dashboard** mit Export- und Löschfunktion
-  **Export als CSV** (Admin)
-  **Speicherung in Supabase DB & Storage**
-  **Rollenbasierte Zugriffskontrolle**
-  **RLS-Sicherheit (Row Level Security)** via Supabase Policies

---

## 4. Technologien

| Bereich      | Technologie              |
|--------------|---------------------------|
| GUI          | JavaFX                    |
| Backend      | Supabase (PostgreSQL + Storage) |
| Sicherheit   | Supabase RLS, BCrypt      |
| Styling      | FXML + CSS                |
| Buildsystem  | Maven                     |
| Design       | Figma                     |
| Versionierung| GitHub                    |

---

## 5. Testfälle

### Mitarbeiter:innen

**Test Case 1:** View past reimbursement requests  
**Steps:** Log in as an employee and navigate to the reimbursement history.  
**Expected Result:** A list of requests is displayed (date, amount, category, status).

**Test Case 2:** View graphical invoice overview  
**Steps:** Log in and open the graphical overview.  
**Expected Result:** A pie or bar chart shows distribution (restaurant/supermarket).

**Test Case 3:** Correct a submitted invoice  
**Steps:** Edit a submitted invoice.  
**Expected Result:** Changes (amount, category) are saved.

**Test Case 4:** Delete a submitted invoice  
**Steps:** Select and delete a `SUBMITTED` invoice.  
**Expected Result:** Invoice is removed.

**Test Case 5:** Display message when no invoices exist  
**Steps:** Log in as a user with no invoices.  
**Expected Result:** System shows “no invoices submitted” message.

---

### Admins

**Test Case 1:** View all submitted invoices  
**Steps:** Log in as admin and open dashboard.  
**Expected Result:** All invoices are listed with full details.

**Test Case 2:** Export invoices as CSV  
**Steps:** Use the export button.  
**Expected Result:** A CSV file with all invoice data is downloaded.

**Test Case 3:** Delete any invoice  
**Steps:** Select any invoice and delete it.  
**Expected Result:** Invoice is permanently removed from the database.

---

> _Letztes Update: 01.05.2025 – Autor: Zeynep Ilhan_





  
