# Praktikum Software Engineering: Lunchify
## Git (mit Github)
## 1. Was ist Git?
Git ist ein verteiltes Versionskontrollsystem, das es ermöglicht, Änderungen am Code zu verfolgen und in Teams effizient zusammenarbeiten.
* Dezentrales System - Jeder Entwickler hat eine eigene vollständige Kopie der Repos.
* Offline-fähig - Man kann lokal arbeiten und später Änderungen hochladen.
## 2. Funktionen von Git
####  Versionskontrolle
* Git speichert und verfolgt alle Versionen eines Projekts.
* Möglichkeit, ältere Versionen wiederherstellen.
####  Branching & Merging
* Parallele Entwicklung möglich.
* Eigene Feature-Branches ohne das Hauptprojekt zu beeinflussen.
* Merging: Sobald die Arbeit in einem Branch abgeschlossen ist, kann er in den Master-Branch "gemerged" (zusammengeführt) werden.
####  Konfliktmanagement:
*  Konflikte erkennen:Wenn zwei Entwickler gleichzeitig Änderungen an der gleichen Codezeile vornehmen, erkennt Git diese Änderungen und den hilft den Entwicklern, den entstandenen Konflikt zu lösen.
####  Remote-Repositories 
* GitHub, GitLab oder Bitbucket für Teamwork.
* Push & Pull: Änderungen hochladen und abrufen.
####  Rollback 
* Änderungen zurücksetzen.

## 3. Wichtige Git-Befehle
Hier sind die wichtigsten Git-Befehle für die täglichen Arbeiten:

* Neues Repository erstellen:
```
git init 
```
* Repository klonen:
 ```
git clone <URL>
```
* Status der Dateien anzeigen:
```
git status
```
* Datei zur Staging Area hinzufügen:
```
git add <Datei>

git add .
```
* Änderungen speichen:
```
git commit -m "Message"
```
* Änderungen hochladen:
```
git push
```
* Neuste Änderungen abrufen:
```
git pull
```
* Neuen Branch erstellen:
```
git branch <Name>
```
* In einem Branch wechseln:
```
git checkout <Branch>
```
* Branch in aktuellen Branch mergen:
```
git merge <Branch>
```
* Commit-Historie anzeigen:
```
git log
```

## 4. Git: Desktop-Anwendungen und Integration von Git in Programmierumgebungen
Git ist ein flexibles Werkzeug, das auf verschiedene Arten genutzt werden kann, einschließlich der Verwendung über die Kommandozeile (CLI - Command Line Interface), mit grafischen Benutzeroberflächen (GUI - Graphical User Interface)
- Das Terminal ist leistungsstark, aber für Anfänger oft nicht intuitiv.
- Grafische Benutzeroberflächen (GUI) erleichtern die Visualisierung von Commits und Branches.
- Entwicklungsumgebungen wie IntelliJ IDEA und Visual Studio Code bieten integrierte Git-Unterstützung.
- Wahl: Abhängig von Kenntnisstand und Anforderungen

#### Überblick: Desktop-Git-Tools im Vergleich

![Überblick: Desktop-Git-Tools im Vergleich](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team3/blob/44eac73d5a3c1b935a009675a8a3d48547058692/git1.png)

### 1. Git in der IDE aktivieren

#### Visual Studio Code
* Falls Git nicht erkannt wird: **Git installieren** → [git-scm.com](https://git-scm.com)
* **Repository initialisieren**  
  1. Öffne ein Projektverzeichnis  
  2. Terminal öffnen (Strg + ö oder Strg + Shift + P → "Git: Initialize Repository")  
  3. Erstes Commit:
     ```
     git add .
     git commit -m "Initial commit"
     ```
  4. GitHub-Repo verknüpfen:
     ```
     git remote add origin <URL>
     git push -u origin main
     ```

#### IntelliJ IDEA
* **Git aktivieren:**  
  * File → Settings → Version Control → Git
* **Projekt mit bestehendem Git-Repo verknüpfen:**  
  * VCS → Enable Version Control Integration → Git auswählen  
* **Änderungen verfolgen:**  
  * Git → Commit → Push

---

### 2. Typische Git-Workflows in der IDE

#### A) Repository klonen
* **VS Code:**  
  * Strg + Shift + P → "Git: Clone"  
  * Repository-URL eingeben  
* **IntelliJ:**  
  * Get from VCS → Repository-URL einfügen  

#### B) Änderungen vornehmen & committen
* **VS Code:**  
  * Git-Panel öffnen (links) → Änderungen sehen  
  * Änderungen "stagen" → Commit-Nachricht eingeben → Commit 
* **IntelliJ:**  
  * Commit-Fenster öffnen (Alt + 0) → Änderungen auswählen → Commit

#### C) Branches erstellen & wechseln
* **VS Code:**  
  * Strg + Shift + P → "Git: Create Branch"  
  * Wechseln mit:  
    ```
    git checkout <branch-name>
    ```  
* **IntelliJ:**  
  * Unten rechts: Branch-Menü → New Branch 
  * Wechseln mit:  
    ```
    git checkout <branch-name>
    ```

#### D) Änderungen pushen & pullen
* **VS Code:**  
  * Änderungen hochladen:  
    ```
    git push origin <branch>
    ```
* **IntelliJ:**  
  * Änderungen hochladen:  
    * VCS → Git → Push 
  * Neueste Änderungen abrufen:  
    * VCS → Git → Pull  

#### E) Code Reviews mit Pull Requests (GitHub)
* **Neuen Branch pushen:**
  ```
  git push -u origin feature-branch
* Auf GitHub eine **Pull Request (PR)** erstellen
* Änderungen reviewen & mergen

#### F) Konflikte lösen in der IDE
* **Konflikt entsteht bei git pull oder Merge:**  
  * **VS Code:**  
    * Konflikt-Datei öffnen  
    * Optionen auswählen (z. B. "Current Change" oder "Incoming Change")  
    * Änderungen speichern  
  * **IntelliJ:**  
    * Merge Conflicts-Fenster öffnen  
    * Änderungen akzeptieren  
* **Nach der Lösung:**  
  ```
  git add .
  git commit -m "Merge conflict resolved"
  git push
