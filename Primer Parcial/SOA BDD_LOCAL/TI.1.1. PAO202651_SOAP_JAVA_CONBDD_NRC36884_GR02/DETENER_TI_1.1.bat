@echo off
REM Cierra las ventanas y clientes abiertos por LEVANTAR_TI_1.1.bat (Payara y MySQL siguen encendidos)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0presentacion\levantar.ps1" -Detener
