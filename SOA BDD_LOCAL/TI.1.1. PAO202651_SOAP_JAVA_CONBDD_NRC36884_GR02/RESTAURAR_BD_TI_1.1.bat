@echo off
REM Vuelve la base eurekabank a los datos iniciales (Crea_BD.sql + Carga_Datos.sql). Pide la clave de root de MySQL.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0presentacion\levantar.ps1" -RestaurarBD
pause
