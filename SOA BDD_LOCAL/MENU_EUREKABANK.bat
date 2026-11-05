@echo off
REM Menu de EurekaBank: se elige el proyecto (TI) y luego que levantar.
REM Uso: doble clic, o directo:  MENU_EUREKABANK.bat 1.2 servidor
REM      Opciones directas: servidor consola escritorio web movil clientes todo
REM      Todos los servidores (TI.1.2, TI.1.3 y TI.1.4):  MENU_EUREKABANK.bat servidores
REM      Detener:  MENU_EUREKABANK.bat detener 1.2   o   MENU_EUREKABANK.bat detener todos
setlocal
set "RAIZ=%~dp0"
set "DIRECTO="
if not "%~1"=="" set "DIRECTO=1"
set "TI=%~1"
set "ACCION=%~2"

where mvn >nul 2>nul || set "PATH=%PATH%;%ProgramFiles%\Apache NetBeans\java\maven\bin"
if not defined ANDROID_HOME if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
if /i "%TI%"=="servidores" goto todos_servidores
if /i "%TI%"=="detener" goto detener_directo
if defined DIRECTO goto preparar

REM ------------------------------------------------------------------ menu de proyectos
:menu_proyecto
cls
echo.
echo   ==============================================
echo     EUREKABANK GR02 - Elija el proyecto
echo   ==============================================
echo     1. TI.1.1  SOAP Java      (MySQL, Payara)
echo     2. TI.1.2  SOAP .NET      (SQL Server)
echo     3. TI.1.3  RESTful Java   (MySQL, Payara)
echo     4. TI.1.4  RESTful .NET   (SQL Server)
echo     5. Todos los servidores (TI.1.2, TI.1.3 y TI.1.4)
echo     6. Detener servidores
echo     0. Salir
echo.
set "OPCION="
set /p "OPCION=  Opcion: "
if "%OPCION%"=="0" goto salir
if "%OPCION%"=="1" set "TI=1.1" & goto preparar
if "%OPCION%"=="2" set "TI=1.2" & goto preparar
if "%OPCION%"=="3" set "TI=1.3" & goto preparar
if "%OPCION%"=="4" set "TI=1.4" & goto preparar
if "%OPCION%"=="5" goto todos_servidores
if "%OPCION%"=="6" goto menu_detener
goto menu_proyecto

REM ------------------------------------------------------------------ detener servidores
:menu_detener
cls
echo.
echo   ==============================================
echo     EUREKABANK GR02 - Detener servidores
echo   ==============================================
echo     1. Servidor TI.1.1  SOAP Java
echo     2. Servidor TI.1.2  SOAP .NET
echo     3. Servidor TI.1.3  RESTful Java
echo     4. Servidor TI.1.4  RESTful .NET
echo     5. Detener todos los servidores
echo     0. Volver
echo.
set "OPCION="
set /p "OPCION=  Opcion: "
if "%OPCION%"=="0" goto menu_proyecto
if "%OPCION%"=="1" call :detener_ti 1.1 & goto detener_fin
if "%OPCION%"=="2" call :detener_ti 1.2 & goto detener_fin
if "%OPCION%"=="3" call :detener_ti 1.3 & goto detener_fin
if "%OPCION%"=="4" call :detener_ti 1.4 & goto detener_fin
if "%OPCION%"=="5" call :detener_todos & goto detener_fin
goto menu_detener
:detener_fin
echo.
pause
goto menu_detener

:detener_directo
set "FALLO="
if /i "%ACCION%"=="todos" (call :detener_todos) else (call :detener_ti %ACCION%)
goto terminar

REM ------------------------------------------------------------------ todos los servidores
:todos_servidores
set "FALLO="
for %%t in (1.2 1.3 1.4) do call :un_servidor %%t
echo.
if defined DIRECTO goto terminar
pause
goto menu_proyecto

:un_servidor
set "TI=%~1"
echo.
echo --- Servidor TI.%TI%
call :cargar_ti
call :servidor
exit /b 0

REM ------------------------------------------------------------------ datos de cada TI
:preparar
call :cargar_ti
if not defined BASE (
    echo TI no valido: "%TI%". Use 1.1, 1.2, 1.3 o 1.4.
    if defined DIRECTO exit /b 1
    pause
    goto menu_proyecto
)
if defined DIRECTO goto ejecutar

REM ------------------------------------------------------------------ menu de acciones
:menu_accion
cls
echo.
echo   ==============================================
echo     TI.%TI% - %SUFIJO%
echo   ==============================================
echo     1. Servidor (con su base de datos)
echo     2. Cliente de consola
echo     3. Cliente de escritorio
echo     4. Cliente web
echo     5. Cliente movil (celular o emulador)
echo     6. Todos los clientes
echo     7. Servidor y todos los clientes
echo     0. Volver
echo.
set "OPCION="
set /p "OPCION=  Opcion: "
set "ACCION="
if "%OPCION%"=="0" goto menu_proyecto
if "%OPCION%"=="1" set "ACCION=servidor"
if "%OPCION%"=="2" set "ACCION=consola"
if "%OPCION%"=="3" set "ACCION=escritorio"
if "%OPCION%"=="4" set "ACCION=web"
if "%OPCION%"=="5" set "ACCION=movil"
if "%OPCION%"=="6" set "ACCION=clientes"
if "%OPCION%"=="7" set "ACCION=todo"
if not defined ACCION goto menu_accion

:ejecutar
set "FALLO="
if /i "%ACCION%"=="servidor" call :servidor
if /i "%ACCION%"=="consola" call :consola
if /i "%ACCION%"=="escritorio" call :escritorio
if /i "%ACCION%"=="web" call :web
if /i "%ACCION%"=="movil" call :movil
if /i "%ACCION%"=="clientes" call :clientes
if /i "%ACCION%"=="todo" call :servidor
if /i "%ACCION%"=="todo" call :clientes
echo.
echo Usuario de prueba: MONSTER / Monster9
if defined DIRECTO goto terminar
echo.
pause
goto menu_accion

:terminar
if defined FALLO exit /b 1
exit /b 0

:salir
endlocal
exit /b 0

REM ================================================================== subrutinas
:detener_todos
call :detener_ti 1.1
call :detener_ti 1.2
call :detener_ti 1.3
call :detener_ti 1.4
call :buscar_payara
if not defined ASADMIN exit /b 0
curl -s -o nul --max-time 3 http://localhost:4848
if errorlevel 1 exit /b 0
echo Apagando Payara, tambien se apagan las webs JSP ...
call "%ASADMIN%" stop-domain domain1 >nul
exit /b 0

:detener_ti
set "TI=%~1"
call :cargar_ti
if not defined BASE (
    echo TI no valido: "%TI%".
    set "FALLO=1"
    exit /b 1
)
if "%TIPO%"=="java" goto detener_java
if "%TI%"=="1.2" set "PUERTOSRV=5090"
if "%TI%"=="1.4" set "PUERTOSRV=5080"
set "HALLADO="
for /f "tokens=2,5" %%a in ('netstat -ano -p tcp ^| findstr LISTENING') do if "%%a"=="0.0.0.0:%PUERTOSRV%" (
    set "HALLADO=1"
    taskkill /F /T /PID %%b >nul 2>nul
)
taskkill /F /T /FI "WINDOWTITLE eq EurekaBank TI %TI% - Servidor*" >nul 2>nul
if defined HALLADO (echo Servidor TI.%TI% detenido.) else (echo Servidor TI.%TI%: no estaba encendido.)
exit /b 0

:detener_java
call :buscar_payara
if not defined ASADMIN (
    echo Servidor TI.%TI%: no se encontro Payara.
    exit /b 0
)
curl -s -o nul --max-time 3 http://localhost:4848
if errorlevel 1 (
    echo Servidor TI.%TI%: no estaba encendido, Payara esta apagado.
    exit /b 0
)
call "%ASADMIN%" --interactive=false undeploy WS_EurekaBank_%SUFIJO% >nul 2>nul
if errorlevel 1 (echo Servidor TI.%TI%: no estaba encendido.) else (echo Servidor TI.%TI% detenido.)
exit /b 0

:buscar_payara
set "ASADMIN="
if defined PAYARA_HOME if exist "%PAYARA_HOME%\bin\asadmin.bat" set "ASADMIN=%PAYARA_HOME%\bin\asadmin.bat"
if not defined ASADMIN if exist "%USERPROFILE%\Payara_Server\bin\asadmin.bat" set "ASADMIN=%USERPROFILE%\Payara_Server\bin\asadmin.bat"
if not defined ASADMIN if exist "%USERPROFILE%\Documents\payara7\bin\asadmin.bat" set "ASADMIN=%USERPROFILE%\Documents\payara7\bin\asadmin.bat"
exit /b 0

:cargar_ti
set "BASE="
if "%TI%"=="1.1" call :datos "TI.1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR02" JAVA_SOAP_GR02 java 8080 soapjava eurekabank-mysql "WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank"
if "%TI%"=="1.2" call :datos "TI.1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR02" DOTNET_SOAP_GR02 dotnet 5190 soapdotnet eurekabank-sqlserver "WSEurekaBank.svc"
if "%TI%"=="1.3" call :datos "TI.1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR02" JAVA_RESTFUL_GR02 java 8080 restjava eurekabank-mysql "WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank"
if "%TI%"=="1.4" call :datos "TI.1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR02" DOTNET_RESTFUL_GR02 dotnet 5180 restdotnet eurekabank-sqlserver "api/eurekabank"
exit /b 0

:datos
set "BASE=%RAIZ%%~1"
set "SUFIJO=%~2"
set "TIPO=%~3"
set "PUERTO=%~4"
set "PAQUETE=ec.edu.monster.eurekabank.%~5"
set "CONTENEDOR=%~6"
set "RUTA=%~7"
exit /b 0

:clientes
call :consola
call :escritorio
call :web
call :movil
exit /b 0

REM ------------------------------------------------------------------ base de datos
REM Se arranca el contenedor si ya existe: repetir compose podria volver a cargar los datos
:base_datos
docker info >nul 2>nul
if errorlevel 1 (
    echo Docker no esta encendido. Abra Docker Desktop y vuelva a intentar.
    set "FALLO=1"
    exit /b 1
)
docker start %CONTENEDOR% >nul 2>nul
if not errorlevel 1 (
    echo Base de datos encendida: %CONTENEDOR%
    exit /b 0
)
echo Creando la base de datos ...
pushd "%BASE%\03. BDD"
docker compose up -d
popd
exit /b 0

REM ------------------------------------------------------------------ servidor
:servidor
call :base_datos
if errorlevel 1 exit /b 1
if "%TI%"=="1.1" set "URLSRV=http://localhost:8080/%RUTA%?wsdl"
if "%TI%"=="1.2" set "URLSRV=http://localhost:5090/%RUTA%?wsdl"
if "%TI%"=="1.3" set "URLSRV=http://localhost:8080/%RUTA%/cuentas"
if "%TI%"=="1.4" set "URLSRV=http://localhost:5080/%RUTA%/login"
REM Si ya responde no se abre otro: chocaria con el puerto
set "HTTP=000"
for /f %%c in ('curl -s -o nul -w "%%{http_code}" --max-time 5 "%URLSRV%"') do set "HTTP=%%c"
if "%HTTP:~0,1%"=="4" if not "%HTTP%"=="404" goto servidor_ya
if "%HTTP:~0,1%"=="2" goto servidor_ya
if "%TIPO%"=="java" goto servidor_java
echo Abriendo el servidor de TI.%TI% ...
start "EurekaBank TI %TI% - Servidor" /D "%BASE%\04. SERVIDOR\WS_EurekaBank_%SUFIJO%\WS_EurekaBank_%SUFIJO%" cmd /k dotnet run --launch-profile http
goto servidor_esperar

:servidor_ya
echo Servidor de TI.%TI% ya estaba encendido: %URLSRV%
exit /b 0

:servidor_java
call :compilar "%BASE%\04. SERVIDOR\WS_EurekaBank_%SUFIJO%" WS_EurekaBank_%SUFIJO%.war
if errorlevel 1 exit /b 1
call :desplegar "%BASE%\04. SERVIDOR\WS_EurekaBank_%SUFIJO%\target\WS_EurekaBank_%SUFIJO%.war"
if errorlevel 1 exit /b 1

:servidor_esperar
echo Esperando el servidor ...
call :esperar "%URLSRV%"
if errorlevel 1 (
    echo El servidor no respondio. Revise su ventana.
    set "FALLO=1"
    exit /b 1
)
echo Servidor listo: %URLSRV%
exit /b 0

REM ------------------------------------------------------------------ clientes
:consola
if "%TIPO%"=="java" goto consola_java
echo Abriendo la consola ...
start "EurekaBank TI %TI% - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k dotnet run
exit /b 0
:consola_java
call :compilar "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" CLICON_EurekaBank_%SUFIJO%.jar
if errorlevel 1 exit /b 1
echo Abriendo la consola ...
start "EurekaBank TI %TI% - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k java -Dstdout.encoding=UTF-8 -jar target\CLICON_EurekaBank_%SUFIJO%.jar
exit /b 0

:escritorio
if "%TIPO%"=="java" goto escritorio_java
echo Abriendo el escritorio ...
start "EurekaBank TI %TI% - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c dotnet run
exit /b 0
:escritorio_java
call :compilar "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" CLIESC_EurekaBank_%SUFIJO%.jar
if errorlevel 1 exit /b 1
echo Abriendo el escritorio ...
start "EurekaBank TI %TI% - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c java -jar target\CLIESC_EurekaBank_%SUFIJO%.jar
exit /b 0

:web
if "%TIPO%"=="java" goto web_java
echo Abriendo la web ...
start "EurekaBank TI %TI% - Web" /min /D "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%" cmd /k dotnet run --launch-profile http
set "URLWEB=http://localhost:%PUERTO%/Login"
goto web_esperar
:web_java
call :compilar "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%" CLIWEB_EurekaBank_%SUFIJO%.war
if errorlevel 1 exit /b 1
call :desplegar "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%\target\CLIWEB_EurekaBank_%SUFIJO%.war"
if errorlevel 1 exit /b 1
set "URLWEB=http://localhost:%PUERTO%/CLIWEB_EurekaBank_%SUFIJO%/login"
:web_esperar
echo Esperando la web ...
call :esperar "%URLWEB%"
if errorlevel 1 (
    echo La web no respondio. Revise su ventana.
    set "FALLO=1"
    exit /b 1
)
echo Web lista: %URLWEB%
start "" "%URLWEB%"
exit /b 0

:movil
call :buscar_celular
if defined CELULAR goto movil_instalar
call :abrir_emulador
if not defined CELULAR (
    echo Movil: no hay celular conectado ni emulador disponible, se omite.
    exit /b 0
)
:movil_instalar
echo Movil: compilando e instalando en una ventana aparte, tarda unos minutos ...
if "%TIPO%"=="java" goto movil_java
start "EurekaBank TI %TI% - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k dotnet build -t:Run -f net10.0-android
exit /b 0
:movil_java
start "EurekaBank TI %TI% - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k ".\gradlew.bat installDebug && "%ADB%" shell monkey -p %PAQUETE% -c android.intent.category.LAUNCHER 1"
exit /b 0

REM ------------------------------------------------------------------ utilidades
REM Compila un proyecto Java solo si aun no tiene el archivo indicado en target
:compilar
if exist "%~1\target\%~2" exit /b 0
where mvn >nul 2>nul
if errorlevel 1 (
    echo No se encontro Maven para compilar %~2.
    set "FALLO=1"
    exit /b 1
)
echo Compilando %~2 ...
pushd "%~1"
call mvn -q package -DskipTests
set "RESULTADO=%ERRORLEVEL%"
popd
if not "%RESULTADO%"=="0" set "FALLO=1"
exit /b %RESULTADO%

REM Enciende Payara si hace falta y despliega el .war indicado
:desplegar
call :buscar_payara
if not defined ASADMIN (
    echo No se encontro Payara. Defina PAYARA_HOME con la carpeta de Payara 7.
    set "FALLO=1"
    exit /b 1
)
curl -s -o nul --max-time 3 http://localhost:4848
if errorlevel 1 (
    echo Encendiendo Payara ...
    call "%ASADMIN%" start-domain domain1 >nul
)
echo Desplegando %~n1 ...
call "%ASADMIN%" --interactive=false deploy --force=true "%~1" >nul
if errorlevel 1 (
    echo No se pudo desplegar %~n1 en Payara.
    set "FALLO=1"
    exit /b 1
)
exit /b 0

REM Espera hasta 3 minutos a que la URL responda (cualquier respuesta HTTP sirve)
:esperar
set /a INTENTOS=0
:esperar_otra
curl -s -o nul --max-time 5 "%~1"
if not errorlevel 1 exit /b 0
set /a INTENTOS+=1
if %INTENTOS% geq 60 exit /b 1
timeout /t 3 /nobreak >nul
goto esperar_otra

:buscar_celular
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB%" set "ADB=%ProgramFiles(x86)%\Android\android-sdk\platform-tools\adb.exe"
set "CELULAR="
if not exist "%ADB%" exit /b 0
for /f "skip=1 tokens=2" %%a in ('call "%ADB%" devices') do if "%%a"=="device" set "CELULAR=1"
exit /b 0

REM Abre el primer emulador disponible y espera a que Android termine de arrancar
:abrir_emulador
set "EMULADOR=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"
if not exist "%EMULADOR%" set "EMULADOR=%ProgramFiles(x86)%\Android\android-sdk\emulator\emulator.exe"
if not exist "%EMULADOR%" exit /b 0
set "AVD="
for /f "delims=" %%a in ('call "%EMULADOR%" -list-avds 2^>nul') do if not defined AVD set "AVD=%%a"
if not defined AVD exit /b 0
echo Abriendo el emulador %AVD%, tarda un momento ...
start "Emulador Android" /min "%EMULADOR%" -avd %AVD% -no-boot-anim
"%ADB%" wait-for-device
set /a INTENTOS=0
:emulador_otra
set "ARRANCADO="
for /f "delims=" %%a in ('call "%ADB%" shell getprop sys.boot_completed 2^>nul') do set "ARRANCADO=%%a"
if "%ARRANCADO%"=="1" goto emulador_listo
set /a INTENTOS+=1
if %INTENTOS% geq 80 exit /b 0
timeout /t 3 /nobreak >nul
goto emulador_otra
:emulador_listo
set "CELULAR=1"
exit /b 0
