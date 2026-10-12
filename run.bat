@echo off
title CarePulse Clinical Management System
echo ========================================================
echo Starting CarePulse Clinical Management System...
echo ========================================================
echo.
cd /d "%~dp0"
mvn spring-boot:run
pause
