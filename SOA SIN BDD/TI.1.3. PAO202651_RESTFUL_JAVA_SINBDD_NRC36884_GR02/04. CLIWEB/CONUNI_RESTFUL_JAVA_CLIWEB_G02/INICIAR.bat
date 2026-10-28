@echo off
chcp 65001 >nul
title Cliente Web RESTFUL - Monsters Inc.
rem El cliente web es un WAR de Jakarta EE: se despliega en Payara junto con el servidor.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\..\04. SERVIDOR\iniciar_servidor.ps1"
start http://localhost:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02
pause
