@echo off
REM Cierra los servidores y clientes abiertos por los LEVANTAR_TI_*.bat
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0presentacion\levantar.ps1" -Detener
