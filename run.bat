@echo off
echo ===================================================
echo     Freelancing.SB Desktop Platform - Launcher
echo ===================================================

"C:\Program Files\Java\jdk-17\bin\java.exe" --module-path "lib\javafx-sdk-17.0.10\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.media,javafx.swing -cp bin com.freelancing.app.Main
