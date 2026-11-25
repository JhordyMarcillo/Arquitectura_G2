@echo off
REM Levanta TI.1.1 completo (servidor + consola + escritorio + web + movil).
REM La IP se cambia en CONFIGURACION_PRESENTACION.txt
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0presentacion\levantar.ps1" -TI 1.1
