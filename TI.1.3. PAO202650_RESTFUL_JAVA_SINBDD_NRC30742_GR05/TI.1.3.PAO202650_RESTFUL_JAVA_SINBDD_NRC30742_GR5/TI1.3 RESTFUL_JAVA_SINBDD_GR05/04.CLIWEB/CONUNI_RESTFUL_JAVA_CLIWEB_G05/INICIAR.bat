@echo off
chcp 65001 >nul
title Cliente Web RESTFUL - Monsters Inc.
color 0A

cls
echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║         CLIENTE WEB RESTFUL - MONSTERS INC.              ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

color 0B
echo [1/4] Verificando Maven...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Maven no encontrado. Instala Maven o agrega al PATH.
    pause
    exit /b 1
)
color 0A
echo [OK] Maven encontrado
echo.

color 0B
echo [2/4] Compilando proyecto (esto puede tardar unos segundos)...
color 0F
call mvn clean package -q -DskipTests 2>nul
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Error al compilar. Ejecuta 'mvn clean package' para ver detalles.
    pause
    exit /b 1
)
color 0A
echo [OK] Compilación exitosa
echo.

color 0B
echo [3/4] Iniciando servidor Tomcat en puerto 8081...
color 0A
echo [OK] Servidor iniciando...
echo.

color 0B
echo [4/4] Abriendo navegador en 3 segundos...
timeout /t 3 /nobreak >nul
start http://localhost:8081/CONUNI_RESTFUL_JAVA_CLIWEB_G09
color 0A
echo [OK] Navegador abierto
echo.

color 0E
echo ╔═══════════════════════════════════════════════════════════╗
echo ║  ✓ SERVIDOR INICIADO                                     ║
echo ║  ✓ URL: http://localhost:8081/CONUNI_RESTFUL_JAVA_CLIWEB_G09║
echo ║  ✓ Presiona Ctrl+C para detener                          ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.
color 0F

call mvn tomcat7:run


