@echo off
REM ============================================================
REM MONSTERS INC. CONVERTER RESTFUL - CLIENTE CONSOLA CON COLORES
REM Sistema de conversión de unidades RESTful con soporte UTF-8
REM ============================================================

REM Configurar codificación UTF-8
chcp 65001 >nul 2>&1

REM Habilitar colores ANSI en Windows (requiere Windows 10 versión 1511 o superior)
reg add HKCU\Console /v VirtualTerminalLevel /t REG_DWORD /d 1 /f >nul 2>&1

REM Configurar variable TERM para que Java detecte colores
set TERM=xterm-256color

REM Limpiar pantalla
cls

REM Mostrar banner
echo.
echo ╔══════════════════════════════════════════════════════════════════════════════╗
echo ║                                                                              ║
echo ║                    MONSTERS INC. CONVERTER RESTFUL                            ║
echo ║                  Sistema de Conversión de Unidades RESTful                      ║
echo ║                                                                              ║
echo ╚══════════════════════════════════════════════════════════════════════════════╝
echo.
echo Configurando terminal para colores y UTF-8...
echo.

REM Verificar Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no está instalado o no está en el PATH
    echo Por favor, instale Java 17 o superior
    pause
    exit /b 1
)

REM Verificar Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven no está instalado o no está en el PATH
    echo Por favor, instale Apache Maven
    pause
    exit /b 1
)

echo ✓ Java detectado
echo ✓ Maven detectado
echo ✓ Codificación UTF-8 configurada
echo ✓ Colores ANSI habilitados
echo.

REM Prueba de caracteres especiales
echo Prueba de caracteres especiales: áéíóúñ ÁÉÍÓÚÑ
echo.

REM Compilar el proyecto primero
echo Compilando proyecto (mvn clean package)...
echo.
call mvn clean package -q
if %errorlevel% neq 0 (
    echo ERROR: Falló la compilación del proyecto
    echo Revise los errores anteriores
    pause
    exit /b 1
)

echo ✓ Proyecto compilado exitosamente
echo.

REM Buscar el JAR generado
set "JAR_FILE=target\CONUNI_RESTFUL_JAVA_CLICON_G09-1.0-SNAPSHOT.jar"
if not exist "%JAR_FILE%" (
    echo ERROR: No se encontró el JAR compilado
    echo Archivo esperado: %JAR_FILE%
    pause
    exit /b 1
)

REM Mostrar información del sistema
echo ══════════════════════════════════════════════════════════════════════════════
echo Información del Sistema:
echo ══════════════════════════════════════════════════════════════════════════════
java -version 2>&1 | findstr /i "version"
echo.
echo Credenciales de acceso:
echo Usuario: MONSTER
echo Contraseña: MONSTER9
echo.
echo Nota: Asegúrese de que el servidor RESTful esté ejecutándose en:
echo http://10.92.232.246:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion
echo.
echo ══════════════════════════════════════════════════════════════════════════════
echo.

REM Pausa breve
timeout /t 2 >nul 2>&1

REM Ejecutar el JAR con colores habilitados
echo Iniciando aplicación...
echo.
echo ══════════════════════════════════════════════════════════════════════════════
echo.

REM Ejecutar el JAR con encoding UTF-8 y variables de entorno para colores
REM La variable TERM ya está configurada arriba, Java la heredará
java -Dfile.encoding=UTF-8 -jar "%JAR_FILE%"

REM Verificar código de salida y mostrar mensaje final
if %errorlevel% neq 0 (
    echo.
    echo ══════════════════════════════════════════════════════════════════════════════
    echo La aplicación finalizó con errores
    echo ══════════════════════════════════════════════════════════════════════════════
) else (
    echo.
    echo ══════════════════════════════════════════════════════════════════════════════
    echo Aplicación finalizada correctamente
    echo ══════════════════════════════════════════════════════════════════════════════
)

echo.
echo Presione cualquier tecla para salir...
pause >nul


