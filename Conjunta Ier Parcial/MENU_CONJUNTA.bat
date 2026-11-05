@echo off
REM Menu de la Conjunta Ier Parcial: BanQuito + TelefQuito (GR02).
REM Uso: doble clic, o directo:  MENU_CONJUNTA.bat ^<accion^> [ip_del_servidor]
REM      Acciones: servidor consola escritorio web movil clientes todo bdd
REM      Ejemplo:  MENU_CONJUNTA.bat consola 192.168.1.20
REM Servidor: al terminar muestra la IP de este equipo para los clientes.
REM Clientes: piden la IP del servidor (Enter = la ultima usada o localhost).
setlocal
set "RAIZ=%~dp0BanQuito_TelefQuito_Gr02"
set "TQ=%RAIZ%\02.TELFQUITO_GR02"
set "ARCHIVO_IP=%TEMP%\conjunta_gr02_ip_servidor.txt"
set "DIRECTO="
if not "%~1"=="" set "DIRECTO=1"
set "ACCION=%~1"
set "IP=%~2"

where mvn >nul 2>nul || set "PATH=%PATH%;%ProgramFiles%\Apache NetBeans\java\maven\bin"
where java >nul 2>nul
if errorlevel 1 if not defined JAVA_HOME if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
if not defined ANDROID_HOME if exist "%LOCALAPPDATA%\Android\Sdk" set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"

echo Buscando la IP de este equipo ...
call :ip_local
if defined DIRECTO goto ejecutar

REM ------------------------------------------------------------------ menu
:menu
cls
echo.
echo   ==============================================
echo     CONJUNTA IER PARCIAL - BANQUITO / TELFQUITO
echo   ==============================================
echo     1. Servidor (BanQuito + TelefQuito en Payara)
echo     2. Cliente de consola
echo     3. Cliente de escritorio
echo     4. Cliente web
echo     5. Cliente movil (celular o emulador)
echo     6. Todos los clientes
echo     7. Servidor y todos los clientes
echo     8. Cargar la base de datos (scripts SQL)
echo     0. Salir
echo.
echo     IP de este equipo: %IPLOCAL%
echo.
set "OPCION="
set /p "OPCION=  Opcion: "
set "ACCION="
set "IP="
if "%OPCION%"=="0" goto salir
if "%OPCION%"=="1" set "ACCION=servidor"
if "%OPCION%"=="2" set "ACCION=consola"
if "%OPCION%"=="3" set "ACCION=escritorio"
if "%OPCION%"=="4" set "ACCION=web"
if "%OPCION%"=="5" set "ACCION=movil"
if "%OPCION%"=="6" set "ACCION=clientes"
if "%OPCION%"=="7" set "ACCION=todo"
if "%OPCION%"=="8" set "ACCION=bdd"
if not defined ACCION goto menu

REM ------------------------------------------------------------------ ejecutar
:ejecutar
set "FALLO="
set "ES_CLIENTE="
for %%c in (consola escritorio web movil clientes) do if /i "%ACCION%"=="%%c" set "ES_CLIENTE=1"
if /i "%ACCION%"=="servidor" goto ejecutar_servidor
if /i "%ACCION%"=="todo" goto ejecutar_todo
if /i "%ACCION%"=="bdd" goto ejecutar_bdd
if not defined ES_CLIENTE (
    echo Accion no valida: "%ACCION%". Use servidor, consola, escritorio, web, movil, clientes, todo o bdd.
    set "FALLO=1"
    if defined DIRECTO goto terminar
    pause
    goto menu
)

REM Clientes: primero la IP del servidor
if not defined IP if not defined DIRECTO call :pedir_ip
if not defined IP set "IP=localhost"
call :limpiar_ip
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
goto fin_accion

:ejecutar_todo
call :servidor
if defined FALLO goto fin_accion
set "IP=localhost"
call :clientes
goto fin_accion

:ejecutar_bdd
call :bdd
goto fin_accion

:fin_accion
echo.
echo Usuario de prueba: Monster / Monster9
if defined DIRECTO goto terminar
echo.
pause
goto menu

:terminar
if defined FALLO exit /b 1
exit /b 0

:salir
endlocal
exit /b 0

REM ================================================================== IP
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
echo   Escriba la IP del equipo donde corre el servidor.
echo   - Mismo equipo: localhost
echo   - Otro equipo: la IP que muestra la opcion 1 en ese equipo
echo   - Enter: %IPDEF%
echo.
set "ENTRADA="
set /p "ENTRADA=  IP del servidor: "
if defined ENTRADA (set "IP=%ENTRADA%") else set "IP=%IPDEF%"
exit /b 0

REM Quita espacios, http:// y el puerto si lo escribieron; recuerda la IP
:limpiar_ip
set "IP=%IP: =%"
set "IP=%IP:http://=%"
set "IP=%IP::8080=%"
set "IP=%IP:/=%"
> "%ARCHIVO_IP%" echo %IP%
exit /b 0

:verificar_servidor
echo.
echo Probando el servidor en http://%IP%:8080/TELFQUITO_SOAP_JAVA ...
curl -s -o nul --max-time 5 "http://%IP%:8080/TELFQUITO_SOAP_JAVA/WSLogin?wsdl"
if not errorlevel 1 (
    echo Servidor encontrado.
    exit /b 0
)
echo.
echo   ATENCION: el servidor no responde en %IP%:8080.
echo   Revise que este encendido ^(opcion 1^), la IP y el Firewall ^(puerto 8080^).
echo   Los clientes de consola y escritorio necesitan el servidor para compilar.
if defined DIRECTO exit /b 0
set "SEGUIR="
set /p "SEGUIR=  Abrir los clientes de todos modos? (S/N): "
if /i "%SEGUIR%"=="S" exit /b 0
exit /b 1

:mostrar_ip
echo.
echo   ==============================================
echo     IP DEL SERVIDOR: %IPLOCAL%
echo   ==============================================
echo     En los clientes de otros equipos escriba: %IPLOCAL%
echo     En este mismo equipo puede usar:          localhost
echo     BanQuito:   http://%IPLOCAL%:8080/BANQUITO_SOAP_JAVA/WSSujetoCredito?wsdl
echo     TelefQuito: http://%IPLOCAL%:8080/TELFQUITO_SOAP_JAVA/WSLogin?wsdl
echo     Si otro equipo no conecta, permita el puerto 8080 en el Firewall.
exit /b 0

REM ================================================================== servidor
:servidor
call :mysql_activo
if errorlevel 1 (
    echo MySQL no responde en el puerto 3306. Enciendalo ^(XAMPP o servicio MySQL^) y vuelva a intentar.
    set "FALLO=1"
    exit /b 1
)
echo MySQL encendido. Si es la primera vez o el dia de la defensa, cargue la base con la opcion 8.
call :compilar "%RAIZ%\01.SERVIDOR_BANQUITO_GR02\BANQUITO_SOAP_JAVA" BanQuito
if errorlevel 1 exit /b 1
call :desplegar "%RAIZ%\01.SERVIDOR_BANQUITO_GR02\BANQUITO_SOAP_JAVA\target\BANQUITO_SOAP_JAVA-1.war" BANQUITO_SOAP_JAVA
if errorlevel 1 exit /b 1
call :esperar "http://localhost:8080/BANQUITO_SOAP_JAVA/WSSujetoCredito?wsdl" BanQuito
if errorlevel 1 exit /b 1
REM TelefQuito se compila con BanQuito ya publicado: lee sus WSDL
call :compilar "%TQ%\01.SERVIDOR\TELFQUITO_SOAP_JAVA" TelefQuito
if errorlevel 1 exit /b 1
call :desplegar "%TQ%\01.SERVIDOR\TELFQUITO_SOAP_JAVA\target\TELFQUITO_SOAP_JAVA-1.war" TELFQUITO_SOAP_JAVA
if errorlevel 1 exit /b 1
call :esperar "http://localhost:8080/TELFQUITO_SOAP_JAVA/WSLogin?wsdl" TelefQuito
if errorlevel 1 exit /b 1
echo Servidores listos.
call :mostrar_ip
exit /b 0

:mysql_activo
powershell -NoProfile -Command "try { $t = New-Object Net.Sockets.TcpClient; $t.Connect('127.0.0.1', 3306); $t.Close(); exit 0 } catch { exit 1 }"
exit /b %ERRORLEVEL%

REM ================================================================== base de datos
:bdd
set "MYSQL="
for /f "delims=" %%a in ('where mysql 2^>nul') do if not defined MYSQL set "MYSQL=%%a"
if not defined MYSQL if exist "C:\xampp\mysql\bin\mysql.exe" set "MYSQL=C:\xampp\mysql\bin\mysql.exe"
if not defined MYSQL for /d %%d in ("%ProgramFiles%\MySQL\MySQL Server *") do if exist "%%d\bin\mysql.exe" set "MYSQL=%%d\bin\mysql.exe"
if not defined MYSQL (
    echo No se encontro mysql.exe. Agreguelo al PATH o cargue los .sql desde Workbench:
    echo   %TQ%\03.BDD
    set "FALLO=1"
    exit /b 1
)
call :mysql_activo
if errorlevel 1 (
    echo MySQL no responde en el puerto 3306. Enciendalo y vuelva a intentar.
    set "FALLO=1"
    exit /b 1
)
set "CLAVE_BDD="
if defined DB_PASSWORD set "CLAVE_BDD=-p%DB_PASSWORD%"
echo Cargando core ^(BanQuito^) ...
"%MYSQL%" -u root %CLAVE_BDD% --default-character-set=utf8mb4 < "%TQ%\03.BDD\01_Core_BanQuito.sql"
if errorlevel 1 goto bdd_error
echo Cargando comercializadora ^(TelefQuito^) ...
"%MYSQL%" -u root %CLAVE_BDD% --default-character-set=utf8mb4 < "%TQ%\03.BDD\02_Comercializadora.sql"
if errorlevel 1 goto bdd_error
echo Base de datos cargada.
exit /b 0
:bdd_error
echo No se pudo cargar la base. Si root tiene clave, ejecute antes:  set DB_PASSWORD=su_clave
set "FALLO=1"
exit /b 1

REM ================================================================== clientes
:clientes
call :consola
call :escritorio
call :web
call :movil
exit /b 0

:consola
call :hay_maven
if errorlevel 1 exit /b 1
echo Abriendo la consola ^(servidor %IP%^) ...
start "TelefQuito - Consola (%IP%)" /D "%TQ%\02.CLICON\TELFQUITO_SOAP_JAVA_CLICON" cmd /k "chcp 65001 >nul & mvn -q compile exec:java -Dservidor.ip=%IP%"
exit /b 0

:escritorio
call :hay_maven
if errorlevel 1 exit /b 1
echo Abriendo el escritorio ^(servidor %IP%^) ...
start "TelefQuito - Escritorio (%IP%)" /min /D "%TQ%\02.CLIESC\BANQUITO_SOAP_JAVA_CLIESC" cmd /k "mvn -q compile exec:java -Dservidor.ip=%IP%"
exit /b 0

:web
where npm >nul 2>nul
if errorlevel 1 (
    echo No se encontro Node.js ^(npm^) para el cliente web.
    set "FALLO=1"
    exit /b 1
)
curl -s -o nul --max-time 3 http://localhost:3000
if not errorlevel 1 (
    echo Ya hay una web abierta en el puerto 3000. Si cambio la IP, cierre esa ventana y vuelva a abrir la web.
    start "" "http://localhost:3000"
    exit /b 0
)
set "WEBDIR=%TQ%\02.CLIWEB\TELFQUITO_SOAP_JAVA_CLIWEB"
if not exist "%WEBDIR%\node_modules" (
    echo Instalando dependencias de la web ...
    pushd "%WEBDIR%"
    call npm install --no-fund --no-audit
    popd
)
echo Abriendo la web ^(servidor %IP%^) ...
set "SERVIDOR_IP=%IP%"
start "TelefQuito - Web (%IP%)" /min /D "%WEBDIR%" cmd /k npm start
set "SERVIDOR_IP="
call :esperar "http://localhost:3000" "la web"
if errorlevel 1 exit /b 1
echo Web lista: http://localhost:3000
start "" "http://localhost:3000"
exit /b 0

:movil
call :buscar_celular
if defined CELULAR goto movil_ip
call :abrir_emulador
if not defined CELULAR (
    echo Movil: no hay celular conectado ni emulador disponible, se omite.
    exit /b 0
)
:movil_ip
REM El celular no puede usar "localhost": se usa la IP del PC (o 10.0.2.2 en el emulador)
set "IPMOVIL=%IP%"
if /i not "%IP%"=="localhost" if not "%IP%"=="127.0.0.1" goto movil_instalar
set "IPMOVIL=%IPLOCAL%"
if defined EMULADO set "IPMOVIL=10.0.2.2"
:movil_instalar
echo Movil: compilando e instalando con servidor %IPMOVIL% en una ventana aparte, tarda unos minutos ...
start "TelefQuito - Movil (%IPMOVIL%)" /D "%TQ%\02.CLIMOV" cmd /k ".\gradlew.bat installDebug -PservidorIp=%IPMOVIL% && "%ADB%" -s %CELULAR% shell am start -n com.example.telfquito_soap_java/.view.LoginActivity"
exit /b 0

REM ================================================================== utilidades
:hay_maven
where mvn >nul 2>nul
if not errorlevel 1 exit /b 0
echo No se encontro Maven ^(mvn^). Instale Maven o NetBeans, o agreguelo al PATH.
set "FALLO=1"
exit /b 1

:compilar
call :hay_maven
if errorlevel 1 exit /b 1
echo Compilando %~2 ...
pushd "%~1"
call mvn -q package -DskipTests
set "RESULTADO=%ERRORLEVEL%"
popd
if not "%RESULTADO%"=="0" (
    echo No se pudo compilar %~2.
    set "FALLO=1"
)
exit /b %RESULTADO%

REM Enciende Payara si hace falta y despliega el .war con su contexto (%2)
:desplegar
set "ASADMIN="
for %%p in ("%PAYARA_HOME%" "%USERPROFILE%\Payara_Server" "%USERPROFILE%\Documents\payara7" "%USERPROFILE%\Documents\payara6" "%USERPROFILE%\payara7" "%USERPROFILE%\payara6" "C:\payara7" "C:\payara6") do if not defined ASADMIN if exist "%%~p\bin\asadmin.bat" set "ASADMIN=%%~p\bin\asadmin.bat"
if not defined ASADMIN (
    echo No se encontro Payara. Defina PAYARA_HOME con la carpeta de Payara, por ejemplo:
    echo   setx PAYARA_HOME "C:\payara6"
    set "FALLO=1"
    exit /b 1
)
curl -s -o nul --max-time 3 http://localhost:4848
if errorlevel 1 (
    echo Encendiendo Payara ...
    call "%ASADMIN%" start-domain domain1 >nul
)
echo Desplegando %~2 ...
call "%ASADMIN%" --interactive=false deploy --force=true --name %~2 --contextroot %~2 "%~1" >nul
if errorlevel 1 (
    echo No se pudo desplegar %~2 en Payara.
    set "FALLO=1"
    exit /b 1
)
exit /b 0

REM Espera hasta 3 minutos a que la URL responda (cualquier respuesta HTTP sirve)
:esperar
echo Esperando %~2 ...
set /a INTENTOS=0
:esperar_otra
curl -s -o nul --max-time 5 "%~1"
if not errorlevel 1 exit /b 0
set /a INTENTOS+=1
if %INTENTOS% geq 60 (
    echo %~2 no respondio en %~1. Revise su ventana.
    set "FALLO=1"
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto esperar_otra

:buscar_celular
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if defined ANDROID_HOME if exist "%ANDROID_HOME%\platform-tools\adb.exe" set "ADB=%ANDROID_HOME%\platform-tools\adb.exe"
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
if defined ANDROID_HOME if exist "%ANDROID_HOME%\emulator\emulator.exe" set "EMULADOR=%ANDROID_HOME%\emulator\emulator.exe"
if not exist "%EMULADOR%" exit /b 0
if not exist "%ADB%" exit /b 0
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
