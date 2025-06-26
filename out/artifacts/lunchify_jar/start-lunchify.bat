@echo off
cd /d %~dp0
java --module-path "C:\Users\ilhan\.m2\repository\org\openjfx\javafx-base\21.0.2;C:\Users\ilhan\.m2\repository\org\openjfx\javafx-controls\21.0.2;C:\Users\ilhan\.m2\repository\org\openjfx\javafx-fxml\21.0.2;C:\Users\ilhan\.m2\repository\org\openjfx\javafx-graphics\21.0.2" --add-modules javafx.controls,javafx.fxml -jar lunchify.jar
pause
