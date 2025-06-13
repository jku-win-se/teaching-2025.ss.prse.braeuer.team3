# Lunchify

## Dokumentation

- [Benutzerdokumentation (User & Admin)](./docs/Lunchify_Benutzerdokumentation.docx)  
- [Systemdokumentation](./docs/Lunchify_Systemdokumentation.docx)

## Projektübersicht
**Lunchify** ist eine Desktop-Anwendung zur Verwaltung und Erstattung von Mitarbeitenden-Rechnungen. Angestellte können ihre Rechnungen (z. B. Restaurant- oder Supermarktbelege) hochladen und deren Status verfolgen. Administratoren erhalten statistische Auswertungen, können Rechnungen prüfen, bearbeiten und Auszahlungen auslösen. Zusätzlich stehen personalisierte Benachrichtigungen und Einstellungen zur Verfügung.

## Features

### Für End-User
- **Rechnung hochladen** (PDF/Bild) und Details erfassen  
- **Verlauf**: Einsicht in zuletzt hochgeladene und „favorisierte“ Rechnungen  
- **Status-Benachrichtigungen**: Glocken-Symbol im Dashboard zeigt Approval/Rejection an  
- **Notification Preferences**: Unter „Einstellungen“ selbst festlegen, welche E-Mail-Benachrichtigungen gewünscht sind  
- **Passwort ändern & Profil aktualisieren** direkt in der App  

### Für Administratoren
- **Dashboard mit Kennzahlen**  
  - Anzahl eingereichter Rechnungen pro Monat  
  - Durchschnittliche Rechnungen je Mitarbeiter  
  - Aufschlüsselung nach Kategorie (Restaurant vs. Supermarkt)  
  - Erstattungsbeträge monatlich und kumuliert über 12 Monate  
- **Rechnungsprüfung & Bearbeitung**: Rechnungen bleiben im Status „submitted“ und werden automatisch als „edited“ markiert, sobald Anpassungen erfolgen  
- **Benutzerverwaltung**: Neue Nutzer anlegen, Passwortrücksetzung erzwingen  
- **Statistische Auswertungen** und Exportmöglichkeit  

## Technologien & Architektur
- **Frontend**: Java 21 + JavaFX 21  
- **Backend-Datenbank**: PostgreSQL  
- **Object Storage**: Supabase Storage (für Rechnung-Dateien)  
- **ORM / DAO-Layer**: JDBC mit PreparedStatements, BCrypt für Passwort-Hashing  
- **Extras**: ControlsFX (ToggleSwitch), JFreeChart für Diagramme  
- **Build & Dependency Management**: Maven  

## Voraussetzungen
- JDK 21 (OpenJDK oder Oracle)  
- Maven 3.6+  
- Lokale oder entkoppelte PostgreSQL-Instanz  
- Supabase Account / Bucket für File-Hosting

## Code-Qualität & Analyse
- SonarQube-Checkstyle in CI
- Automatisierte Builds auf GitHub Actions

## UML Diagramm
![UML_prse](https://github.com/user-attachments/assets/368fc4b0-d02f-4a65-980c-f76ed93e2c02)





