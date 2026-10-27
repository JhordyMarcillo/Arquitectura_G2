@echo off
echo ========================================
echo   CLIENTE WEB MONSTER - Conversiones
echo ========================================
echo.
echo Iniciando servidor Flask...
echo URL: http://localhost:3000
echo.
echo Credenciales:
echo   Usuario: MONSTER
echo   Contraseña: Monster9
echo.
echo El login se abrira automaticamente...
echo Presiona CTRL+C para detener el servidor
echo ========================================
echo.
start http://localhost:3000
python app.py
pause
