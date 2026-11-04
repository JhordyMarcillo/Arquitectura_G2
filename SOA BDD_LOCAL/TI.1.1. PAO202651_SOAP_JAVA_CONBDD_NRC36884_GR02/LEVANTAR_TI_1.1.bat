@echo off
REM Levanta la TI.1.1 completa: MySQL + servidor SOAP en Payara + web + consola + escritorio + movil.
REM La IP y el modo del celular se cambian en CONFIGURACION_PRESENTACION.txt
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0presentacion\levantar.ps1"
