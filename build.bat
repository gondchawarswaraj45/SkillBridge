@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ===================================================
echo     Freelancing.SB Desktop Platform - Build Script
echo ===================================================

echo Compiling Java 17 JavaFX application via Maven...
call mvn clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo BUILD SUCCESSFUL!
) else (
    echo.
    echo BUILD FAILED! Check error output above.
)
pause


