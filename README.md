#UML-Diagramm


![UML_prse](https://github.com/user-attachments/assets/368fc4b0-d02f-4a65-980c-f76ed93e2c02)



# Lunchify – Projektdokumentation

## System Overview

Lunchify is a desktop application developed in JavaFX for managing and reimbursing lunch-related expenses. It allows employees to upload receipts for lunch expenses and receive reimbursements based on predefined rules. The system includes user authentication, role management (admin/user), a reimbursement calculation logic, an invoice history, and an admin dashboard with data export options.

## Architekturübersicht

### Views

- `LoginView.fxml`
- `DashboardView.fxml`
- `UploadInvoiceView.fxml`
- `RequestHistoryView.fxml`
- `SettingsView.fxml`
- `AdminDashboardView.fxml`
- `ForgotPasswordView.fxml`
- `StarredView.fxml`

### Controller

- `LoginController.java`
- `UserDashboardController.java`
- `AddInvoiceController.java`
- `RequestHistoryController.java`
- `AdminDashboardController.java`
- `SettingsController.java`
- `StarredController.java`

### Model

- `Benutzer.java`
- `Rechnung.java`
- `Session.java`
- `Notification.java`
- `Konfiguration.java`
- `Rueckerstattungsantrag.java`

### Datenbankmodell

Die Anwendung verwendet eine Supabase-Datenbank mit den folgenden Tabellen:

- `benutzer` (id, email, password, name, rolle)
- `rechnung` (id, user_id, file_url, type, amount, status, upload_date)
- `notification` (id, user_id, message, timestamp, erstatteter_betrag)
- `konfiguration` (id, aktueller_rueckerstattungsbetrag)
- `report` (id, typ)
- `anomalie` (id, benutzer_id, rechnung_id, status)
- `rueckerstattungsantrag` (benutzer_id, rechnungs_id, uebermittlungsdatum)

## Benutzerrollen

- **Admin:** Zugriff auf alle Benutzer und Rechnungen, Exportfunktionen, Admin-Dashboard
- **User (Mitarbeiter):** Rechnungen hochladen, eigene Anfragen einsehen, bearbeiten, löschen, Einstellungen ändern

## Funktionale Anforderungen

- Upload von Rechnungen (.pdf, .jpg, .png)
- Manuelle Klassifizierung (Restaurant / Supermarkt)
- Automatische Berechnung des Erstattungsbetrags
- Anzeige vergangener Anfragen
- Admin-Export (CSV)
- Admin kann Rechnungen löschen
- Anzeige von Charts zur Verteilung der Rechnungen

## Testfälle

### Admin-Funktionalität

#### Test Case 1: Verify that the admin dashboard displays all submitted invoices.
**Steps:** Log in as an administrator and navigate to the dashboard.  
**Expected Result:** All submitted invoices are displayed with the correct details.

#### Test Case 2: Verify that the export functionality works correctly.
**Steps:** Export the invoice data in CSV format.  
**Expected Result:** The exported file contains all relevant invoice details.

#### Test Case 3: Verify that an administrator can delete an invoice.
**Steps:** Select an invoice and choose the delete option.  
**Expected Result:** The invoice is permanently removed from the system.

### User-Funktionalität

#### Test Case 1: Verify that an employee can view their past reimbursement requests.
**Steps:** Log in as an employee and navigate to the reimbursement history section.  
**Expected Result:** A list of past reimbursement requests is displayed with details such as submission date, amount, classification, and status.

#### Test Case 2: Verify that the system displays a graphical overview of invoices.
**Steps:** Log in as an employee and navigate to the graphical overview section.  
**Expected Result:** A pie chart or bar chart shows the distribution of invoices between restaurants and supermarkets.

#### Test Case 3: Verify that an employee can correct a submitted invoice.
**Steps:** Log in as an employee, navigate to the reimbursement history, select an invoice, and make corrections (e.g., change the amount or classification).  
**Expected Result:** The corrections are saved, and the updated details are displayed.

#### Test Case 4: Verify that an employee can delete a submitted invoice.
**Steps:** Log in as an employee, navigate to the reimbursement history, select an invoice, and delete it.  
**Expected Result:** The invoice is removed from the history.

#### Test Case 5: Verify the system's response when no reimbursement requests have been submitted.
**Steps:** Log in as an employee with no past reimbursement requests and navigate to the reimbursement history section.  
**Expected Result:** The system displays a message indicating that no reimbursement requests have been submitted.
