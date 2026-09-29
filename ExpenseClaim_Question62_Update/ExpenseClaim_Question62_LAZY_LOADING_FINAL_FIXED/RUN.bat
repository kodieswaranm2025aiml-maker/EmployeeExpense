@echo off
cd /d "%~dp0"
echo.
echo ===============================================
echo ExpenseClaim Question 62 - FINAL FIXED
echo Starting Spring Boot on http://localhost:8080/
echo ===============================================
echo.
start "ExpenseClaim Server" cmd /k "cd /d %~dp0 && mvn clean spring-boot:run"
echo Waiting for Spring Boot to start...
timeout /t 12 /nobreak >nul
start "" http://localhost:8080/
