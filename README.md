# Praktikum Software Engineering: Lunchify
## Git (mit GitHub)
## 1. Was ist Git?
Git ist ein verteiltes Versionskontrollsystem, das es ermöglicht, Änderungen am Code zu verfolgen und in Teams effizient zusammenarbeiten.
* Dezentrales System - Jeder Entwickler hat eine eigene vollständige Kopie der Repos
* Offline-fähig - Man kann lokal arbeiten und später Änderungen hochladen.
## 2. Funktionen von Git
####  Versionierung & Historie
* Jeder Code-Änderung wird gespeichert (Commit)
* Möglichkeit, ältere Versionen wiederherstellen
####  Branching & Merging
* Parallele Entwicklung möglich
* Eigene Feature-Branches ohne das Hauptprojekt zu beeinflussen
* Merging: Änderungen aus einem Branche in einen anderen übernehmen
####  Staging Area
* Arbeitsbereich (Working Directory) -> Staging Area -> Repository (Commits)
* Änderungen können erst geprüft werden, bevor sie final gespeichert werden
####  Remote-Repositories & Zusammenarbeit
* GitHub, GitLab oder Bitbucket für Teamwork
* Push & Pull: Änderungen hochladen und abrufen
* Pull Requests & Code-Reviews: Zusammenarbeit im Team
####  Rollback & Fehlerkorrektur
* Änderungen zurücksetzen
* Letzten Commit ändern
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

## 4. Integration von Git in Programmierumgebungen

### 1. Git in der IDE aktivieren

#### Visual Studio Code
* Falls Git nicht erkannt wird: **Git installieren** → [git-scm.com](https://git-scm.com)
* **Repository initialisieren**  
  1. Öffne ein Projektverzeichnis  
  2. Terminal öffnen (`Strg + ö` oder `Strg + Shift + P` → "Git: Initialize Repository")  
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
  * `File` → `Settings` → `Version Control` → `Git`
* **Projekt mit bestehendem Git-Repo verknüpfen:**  
  * `VCS` → `Enable Version Control Integration` → Git auswählen  
* **Änderungen verfolgen:**  
  * `Git` → `Commit` → `Push`

---

### 2. Typische Git-Workflows in der IDE

#### A) Repository klonen
* **VS Code:**  
  * `Strg + Shift + P` → "Git: Clone"  
  * Repository-URL eingeben  
* **IntelliJ:**  
  * `Get from VCS` → Repository-URL einfügen  

#### B) Änderungen vornehmen & committen
* **VS Code:**  
  * Git-Panel öffnen (links) → Änderungen sehen  
  * Änderungen "stagen" → Commit-Nachricht eingeben → `✔️ Commit`  
* **IntelliJ:**  
  * `Commit`-Fenster öffnen (`Alt + 0`) → Änderungen auswählen → `Commit`

#### C) Branches erstellen & wechseln
* **VS Code:**  
  * `Strg + Shift + P` → "Git: Create Branch"  
  * Wechseln mit:  
    ```
    git checkout <branch-name>
    ```  
* **IntelliJ:**  
  * Unten rechts: Branch-Menü → `New Branch`  
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
    * `VCS` → `Git` → `Push`  
  * Neueste Änderungen abrufen:  
    * `VCS` → `Git` → `Pull`  

#### E) Code Reviews mit Pull Requests (GitHub)
* **Neuen Branch pushen:**
  ```
  git push -u origin feature-branch
* Auf GitHub eine **Pull Request (PR)** erstellen
* Änderungen reviewen & mergen

#### F) Konflikte lösen in der IDE
* **Konflikt entsteht bei `git pull` oder Merge:**  
  * **VS Code:**  
    * Konflikt-Datei öffnen  
    * Optionen auswählen (z. B. "Current Change" oder "Incoming Change")  
    * Änderungen speichern  
  * **IntelliJ:**  
    * `Merge Conflicts`-Fenster öffnen  
    * Änderungen akzeptieren  
* **Nach der Lösung:**  
  ```
  git add .
  git commit -m "Merge conflict resolved"
  git push
