# Lunchify

## Dokumentation

- [Benutzerdokumentation (User & Admin)](./docs/Benutzerdoku_v1.4.pdf)  
- [Systemdokumentation](./docs/Systemdokumentation%20Lunchify_v1.2.pdf)

# Lunchify

## Einleitung
Lunchify ist eine Desktop-Anwendung zur Verwaltung und Erstattung von Mitarbeiter-Rechnungen. Mitarbeitende können ihre Belege (z. B. Restaurant- oder Supermarkt-Rechnungen) hochladen und den Status verfolgen. Administratoren erhalten statistische Auswertungen, können Rechnungen prüfen, bearbeiten und Auszahlungen anstoßen. Personalisierte Benachrichtigungen und Einstellungen runden das Nutzererlebnis ab.

## Umgesetzte Anforderungen
* **Rechnungen hochladen** inkl. PDF und Detail-Erfassung  
* **Verlauf einsehen**: zuletzt hochgeladene & „favorisierte“ Rechnungen  
* **Status-Benachrichtigungen** per Glocken-Symbol (Approval/Reject)  
* **Notification Preferences** unter „Einstellungen“  
* **Passwort ändern** & Profil aktualisieren  
* **Dashboard für Admins** mit Kennzahlen:
  * Anzahl eingereichter Rechnungen pro Monat  
  * Ø Rechnungen pro Mitarbeiter  
  * Aufschlüsselung nach Kategorie (Restaurant vs. Supermarkt)  
  * Summe erstattet über die letzten 12 Monate  
* **Rechnung prüfen & bearbeiten**:
  * Status bleibt „submitted“, automatisch „accepted“ (kein manuelles Approve mehr)  
  * Admin-Edit setzt Status auf „edited“ und löst Benachrichtigung aus  
* **Export-Funktionen**: CSV, PDF, JSON, XML  
* **User- & Rollenverwaltung** (Admin-Bereich)  
* **Tests**: JUnit-Testfälle für Kernfunktionen  
* **Code-Qualität**: SonarQube-Checks, automatisierte Builds via GitHub Actions  

## Überblick über die Applikation aus Benutzersicht
1. **Login** mit E-Mail & Passwort  
2. **Home-Dashboard**  
   - Übersicht „Starred“ & „Recently Viewed“  
   - Glocken-Icon für neue Benachrichtigungen  
3. **Add Invoice**  
   - PDF/Belegdaten hochladen  
4. **Requests**  
   - Historie aller eingereichten Rechnungen  
5. **Settings**  
   - Notification Preferences (Approval, Rejection)  
   - Passwort ändern  
   - Profil-Update  
6. **Benachrichtigungen**  
   - Pop-up-Liste aller Notifications

## Überblick über die Applikation aus Entwicklersicht

### Entwurf

#### UML–Diagramm  
![UML Diagramm](docs/uml/Lunchify_UML.png)

#### Verwendete Design Pattern(s)
* **Model–View–Controller** (JavaFX + FXML)  
* **DAO** (JDBC/PreparedStatements)  
* **Observer** (für Live-Updates im UI, z. B. TableView)  

### Wichtige Design-Entscheidungen
1. **Persistenz mit JDBC statt ORM**  
   - **Entscheidung**: Direkter JDBC-Zugriff via DAO-Layer  
   - **Alternativen**: JPA/Hibernate  
   - **Begründung**: Leichterer Einstieg, volle Kontrolle über SQL, geringere Abhängigkeiten  
   - **Konsequenzen**: Mehr Boilerplate-Code, klare Trennung DB-Logik  
2. **Supabase für File-Storage**  
   - **Entscheidung**: Belegdaten in Supabase-Bucket  
   - **Alternativen**: Lokales Filesystem  
   - **Begründung**: Skalierbar und cloudbasiert  
3. **Status-Enum erweitert um `EDITED`**  
   - **Entscheidung**: Zusätzlicher Status statt `APPROVED`  
   - **Alternativen**: `APPROVED` überschreiben  
   - **Begründung**: Nachvollziehbarkeit, klare Historie  

### Implementierung
* **Frontend**: Java 21 + JavaFX 21 + FXML + ControlsFX  
* **Backend**: PostgreSQL  
* **Storage**: Supabase  
* **DAO-Layer**: JDBC + PreparedStatements  
* **Security**: BCrypt für Passwort-Hashing  
* **Build & Dependency Management**: Maven  

**Projektstruktur**  
src/
├── main/
│ ├── java/
│ │ ├── controller/
│ │ ├── model/
│ │ └── util/
│ └── resources/
│ ├── view/
│ └── css/
└── test/
└── java/


## Code-Qualität
* **SonarQube**–Checks (CI)  
* **Checkstyle**: Einhaltung unseres Java-Coding-Standards  
* **GitHub Actions**: Automatisierte Builds & Tests bei jedem Push  

## Testen
* **JUnit 5**–Tests in `src/test/java`  
* **Testabdeckung**  
  * Login/Session-Handling  
  * InvoiceDAO–Operationen  
  * NotificationDAO  
  * Controller-Logik (Filter, Edit, Reject)  
* Ausgewählte Akzeptanztests siehe [Testfälle](docs/Testplan_Sonderfallbehandlungen%20Lunchify.pdf)

## JavaDoc

## Installationsanleitung
1. **Voraussetzungen**  
   - JDK 21  
   - Maven 3.6+  
   - PostgreSQL-Server  
   - Supabase-Account & Bucket  
2. **Repository klonen**  
   ```bash
   git clone https://github.com/DEIN_ORG/lunchify.git
   cd lunchify
3. Datenbank einrichten  
   - Schema erstellen: db/schema.sql ausführen
   - Umgebungsvariablen setzen:
    ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/lunchify
    export DB_USER=deinuser
    export DB_PASS=deinpass
    export SUPABASE_URL=https://...
    export SUPABASE_KEY=...
4. Build & Run
   ```bash
   mvn clean package
   mvn javafx:run
5. Login
   - Standard-Admin: admin@lunchify.at / admin123
   - Standard-User: user@lunchify.at / user123






