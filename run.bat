@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ===================================================
echo        SkillBridge Desktop Platform - Launcher
echo ===================================================

call mvn javafx:run


