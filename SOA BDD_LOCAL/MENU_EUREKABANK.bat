@echo off
REM Menu de EurekaBank: se elige el proyecto (TI) y luego que levantar.
REM Uso: doble clic, o directo:  MENU_EUREKABANK.bat 1.2 servidor
REM      Opciones directas: servidor consola escritorio web movil clientes todo
REM      Clientes contra otro equipo:  MENU_EUREKABANK.bat 1.2 consola 192.168.1.20
REM      El servidor muestra la IP de este equipo; los clientes piden la IP del servidor.
REM      Todos los servidores (TI.1.2, TI.1.3 y TI.1.4):  MENU_EUREKABANK.bat servidores
REM      Detener:  MENU_EUREKABANK.bat detener 1.2   o   MENU_EUREKABANK.bat detener todos
setlocal
set "RAIZ=%~dp0"
set "DIRECTO="
if not "%~1"=="" set "DIRECTO=1"
set "TI=%~1"
set "ACCION=%~2"
set "IP=%~3"
set "ARCHIVO_IP=%TEMP%\eurekabank_gr02_ip_servidor.txt"

where mvn >nul 2>nul || set "PATH=%PATH%;%ProgramFiles%\Apache NetBeans\java\maven\bin"
if not defined ANDROID_HOME if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
call :ip_local
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
call :mostrar_ip 1.2 1.3 1.4
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
echo     IP de este equipo: %IPLOCAL%
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
if /i "%ACCION%"=="servidor" goto ejecutar_servidor
if /i "%ACCION%"=="todo" goto ejecutar_todo
REM Clientes: primero la IP del servidor
if not defined IP if not defined DIRECTO call :pedir_ip
if not defined IP set "IP=localhost"
call :limpiar_ip
call :urls_cliente
call :verificar_servidor
if errorlevel 1 goto fin_accion
if /i "%ACCION%"=="consola" call :consola
if /i "%ACCION%"=="escritorio" call :escritorio
if /i "%ACCION%"=="web" call :web
if /i "%ACCION%"=="movil" call :movil
if /i "%ACCION%"=="clientes" call :clientes
goto fin_accion

:ejecutar_servidor
call :servidor
if not defined FALLO call :mostrar_ip %TI%
goto fin_accion

:ejecutar_todo
call :servidor
if defined FALLO goto fin_accion
call :mostrar_ip %TI%
set "IP=localhost"
call :urls_cliente
call :clientes

:fin_accion
echo.
echo Usuario de prueba: MONSTER / Monster9
if defined DIRECTO goto terminar
echo.
pause
set "IP="
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
echo Abriendo la consola ^(servidor %URLCLI%^) ...
start "EurekaBank TI %TI% - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k dotnet run -- %URLCLI%
exit /b 0
:consola_java
call :compilar "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" CLICON_EurekaBank_%SUFIJO%.jar
if errorlevel 1 exit /b 1
echo Abriendo la consola ^(servidor %URLCLI%^) ...
start "EurekaBank TI %TI% - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k java -Dstdout.encoding=UTF-8 -jar target\CLICON_EurekaBank_%SUFIJO%.jar %URLCLI%
exit /b 0

:escritorio
if "%TIPO%"=="java" goto escritorio_java
echo Abriendo el escritorio ^(servidor %URLCLI%^) ...
start "EurekaBank TI %TI% - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c dotnet run -- %URLCLI%
exit /b 0
:escritorio_java
call :compilar "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" CLIESC_EurekaBank_%SUFIJO%.jar
if errorlevel 1 exit /b 1
echo Abriendo el escritorio ^(servidor %URLCLI%^) ...
start "EurekaBank TI %TI% - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c java -jar target\CLIESC_EurekaBank_%SUFIJO%.jar %URLCLI%
exit /b 0

:web
if "%TIPO%"=="java" goto web_java
echo Abriendo la web ^(servidor %URLCLI%^) ...
REM La web .NET lee "UrlServidor" de su configuracion: la variable de entorno la reemplaza
set "UrlServidor=%URLCLI%"
start "EurekaBank TI %TI% - Web" /min /D "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%" cmd /k dotnet run --launch-profile http
set "UrlServidor="
set "URLWEB=http://localhost:%PUERTO%/Login"
set "URLABRIR=%URLWEB%"
goto web_esperar
:web_java
call :compilar "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%" CLIWEB_EurekaBank_%SUFIJO%.war
if errorlevel 1 exit /b 1
call :desplegar "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%\target\CLIWEB_EurekaBank_%SUFIJO%.war"
if errorlevel 1 exit /b 1
set "URLWEB=http://localhost:%PUERTO%/CLIWEB_EurekaBank_%SUFIJO%/login"
set "URLABRIR=%URLWEB%?servidor=%URLCLI%"
:web_esperar
echo Esperando la web ...
call :esperar "%URLWEB%"
if errorlevel 1 (
    echo La web no respondio. Revise su ventana.
    set "FALLO=1"
    exit /b 1
)
echo Web lista: %URLWEB%
start "" "%URLABRIR%"
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
REM El celular no puede usar "localhost": se usa la IP del PC (o 10.0.2.2 en el emulador)
set "IPMOVIL=%IP%"
if /i not "%IP%"=="localhost" if not "%IP%"=="127.0.0.1" goto movil_url
set "IPMOVIL=%IPLOCAL%"
if defined EMULADO set "IPMOVIL=10.0.2.2"
:movil_url
set "URLMOVIL=http://%IPMOVIL%:%PUERTOSRV%/%RUTA%"
echo Movil: compilando e instalando con servidor %URLMOVIL% en una ventana aparte, tarda unos minutos ...
REM La app se abre con la URL en el intent (extra "urlServidor")
if "%TIPO%"=="java" goto movil_java
start "EurekaBank TI %TI% - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k "dotnet build -t:Run -f net10.0-android && "%ADB%" -s %CELULAR% shell am start -S -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -p %PAQUETE% --es urlServidor %URLMOVIL%"
exit /b 0
:movil_java
start "EurekaBank TI %TI% - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k ".\gradlew.bat installDebug && "%ADB%" -s %CELULAR% shell am start -S -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -p %PAQUETE% --es urlServidor %URLMOVIL%"
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
set "EMULADO="
if not exist "%ADB%" exit /b 0
for /f "skip=1 tokens=1,2" %%a in ('call "%ADB%" devices') do if "%%b"=="device" if not defined CELULAR set "CELULAR=%%a"
if not defined CELULAR exit /b 0
if "%CELULAR:~0,9%"=="emulator-" set "EMULADO=1"
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
call :buscar_celular
exit /b 0

REM ================================================================== IP del servidor
REM IP de este equipo: la de la tarjeta de red que tiene puerta de enlace
:ip_local
set "IPLOCAL="
for /f "usebackq delims=" %%a in (`powershell -NoProfile -Command "$c=@(Get-NetIPConfiguration).Where({$_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up'}); if($c){$c[0].IPv4Address[0].IPAddress}"`) do set "IPLOCAL=%%a"
if not defined IPLOCAL set "IPLOCAL=127.0.0.1"
exit /b 0

:pedir_ip
set "IPDEF=localhost"
if exist "%ARCHIVO_IP%" set /p IPDEF=<"%ARCHIVO_IP%"
echo.
echo   Escriba la IP del equipo donde corre el servidor de TI.%TI%.
echo   - Mismo equipo: localhost
echo   - Otro equipo: la IP que muestra la opcion 1 en ese equipo
echo   - Enter: %IPDEF%
echo.
set "ENTRADA="
set /p "ENTRADA=  IP del servidor: "
if defined ENTRADA (set "IP=%ENTRADA%") else set "IP=%IPDEF%"
exit /b 0

REM Quita espacios, http://, el puerto y la ruta si los escribieron; recuerda la IP
:limpiar_ip
set "IP=%IP: =%"
set "IP=%IP:http://=%"
for /f "tokens=1 delims=:/" %%a in ("%IP%") do set "IP=%%a"
> "%ARCHIVO_IP%" echo %IP%
exit /b 0

REM URL del servicio de este TI en la IP indicada (URLCLI) y una URL para probarlo
:urls_cliente
set "PUERTOSRV=8080"
if "%TI%"=="1.2" set "PUERTOSRV=5090"
if "%TI%"=="1.4" set "PUERTOSRV=5080"
set "URLCLI=http://%IP%:%PUERTOSRV%/%RUTA%"
set "URLPRUEBA=%URLCLI%?wsdl"
if "%TI%"=="1.3" set "URLPRUEBA=%URLCLI%/cuentas"
if "%TI%"=="1.4" set "URLPRUEBA=%URLCLI%/login"
exit /b 0

:verificar_servidor
echo.
echo Probando el servidor %URLCLI% ...
curl -s -o nul --max-time 5 "%URLPRUEBA%"
if not errorlevel 1 (
    echo Servidor encontrado.
    exit /b 0
)
echo.
echo   ATENCION: el servidor de TI.%TI% no responde en %IP%:%PUERTOSRV%.
echo   Revise que este encendido ^(opcion 1^), la IP y el Firewall ^(puerto %PUERTOSRV%^).
if defined DIRECTO exit /b 0
set "SEGUIR="
set /p "SEGUIR=  Abrir los clientes de todos modos? (S/N): "
if /i "%SEGUIR%"=="S" exit /b 0
exit /b 1

REM Muestra la IP de este equipo y la URL de cada TI indicado
:mostrar_ip
echo.
echo   ==============================================
echo     IP DEL SERVIDOR: %IPLOCAL%
echo   ==============================================
echo     En los clientes de otros equipos escriba: %IPLOCAL%
echo     En este mismo equipo puede usar:          localhost
for %%t in (%*) do call :mostrar_url %%t
echo     Si otro equipo no conecta, permita el puerto en el Firewall de Windows.
exit /b 0

:mostrar_url
set "TI=%~1"
call :cargar_ti
set "IP_ANTES=%IP%"
set "IP=%IPLOCAL%"
call :urls_cliente
set "IP=%IP_ANTES%"
echo     TI.%TI%: %URLCLI%
exit /b 0
