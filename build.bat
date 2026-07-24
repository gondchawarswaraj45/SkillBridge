@echo off
echo ===================================================
echo     Freelancing.SB Desktop Platform - Build Script
echo ===================================================

if not exist bin mkdir bin

echo Compiling Java 17 JavaFX source files...
"C:\Program Files\Java\jdk-17\bin\javac.exe" -encoding UTF-8 --module-path "lib\javafx-sdk-17.0.10\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web,javafx.media,javafx.swing -d bin src\com\freelancing\config\*.java src\com\freelancing\model\*.java src\com\freelancing\db\*.java src\com\freelancing\service\*.java src\com\freelancing\ui\*.java src\com\freelancing\app\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo BUILD SUCCESSFUL! All classes compiled to bin\
) else (
    echo.
    echo BUILD FAILED! Check error output above.
)
pause
