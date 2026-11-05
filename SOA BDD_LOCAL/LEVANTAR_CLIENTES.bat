@echo off
REM Levanta los clientes EurekaBank de un TI: consola, escritorio, web y, si hay un
REM celular o emulador conectado, movil. No levanta el servidor ni la base de datos.
REM Uso: doble clic y elegir el TI, o  LEVANTAR_CLIENTES.bat 1.2 | 1.3 | 1.4
setlocal
set "RAIZ=%~dp0"
set "TI=%~1"
if defined TI goto validar

echo.
echo   Clientes EurekaBank GR02
echo     1.2  SOAP .NET
echo     1.3  RESTful Java
echo     1.4  RESTful .NET
echo.
set /p "TI=  Que TI desea levantar? "

:validar
if "%TI%"=="1.2" goto ti12
if "%TI%"=="1.3" goto ti13
if "%TI%"=="1.4" goto ti14
echo TI no valido: "%TI%". Use 1.2, 1.3 o 1.4.
pause
exit /b 1

REM ------------------------------------------------------------------ TI.1.2 SOAP .NET
:ti12
set "BASE=%RAIZ%TI.1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR02"
set "SUFIJO=DOTNET_SOAP_GR02"
set "PUERTO=5190"
goto dotnet

REM ------------------------------------------------------------------ TI.1.4 RESTful .NET
:ti14
set "BASE=%RAIZ%TI.1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR02"
set "SUFIJO=DOTNET_RESTFUL_GR02"
set "PUERTO=5180"
goto dotnet

:dotnet
where dotnet >nul 2>nul || (echo No se encontro dotnet. Instale .NET 10. & pause & exit /b 1)
echo Abriendo consola, escritorio y web de TI.%TI% ...
start "EurekaBank TI %TI% - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k dotnet run
start "EurekaBank TI %TI% - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c dotnet run
start "EurekaBank TI %TI% - Web" /min /D "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%" cmd /k dotnet run --launch-profile http
set "URLWEB=http://localhost:%PUERTO%/Login"
call :esperar_web
call :movil_dotnet
goto fin

REM ------------------------------------------------------------------ TI.1.3 RESTful Java
:ti13
set "BASE=%RAIZ%TI.1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR02"
set "SUFIJO=JAVA_RESTFUL_GR02"
set "PUERTO=8180"
where java >nul 2>nul || (echo No se encontro java. Instale Java 17 o superior. & pause & exit /b 1)
call :compilar_java CLICON || goto error
call :compilar_java CLIESC || goto error
call :compilar_java CLIWEB war || goto error
echo Abriendo consola y escritorio de TI.1.3 ...
start "EurekaBank TI 1.3 - Consola" /D "%BASE%\04. CLICON\CLICON_EurekaBank_%SUFIJO%" cmd /k java -Dstdout.encoding=UTF-8 -jar target\CLICON_EurekaBank_%SUFIJO%.jar
start "EurekaBank TI 1.3 - Escritorio" /min /D "%BASE%\04. CLIESC\CLIESC_EurekaBank_%SUFIJO%" cmd /c java -jar target\CLIESC_EurekaBank_%SUFIJO%.jar
call :web_java || goto error
set "URLWEB=http://localhost:%PUERTO%/CLIWEB_EurekaBank_%SUFIJO%/login"
call :esperar_web
call :movil_java
goto fin

REM Compila un cliente Java solo si aun no tiene su .jar o .war
:compilar_java
set "PROY=%BASE%\04. %~1\%~1_EurekaBank_%SUFIJO%"
set "EXT=%~2"
if not defined EXT set "EXT=jar"
if exist "%PROY%\target\%~1_EurekaBank_%SUFIJO%.%EXT%" exit /b 0
where mvn >nul 2>nul || (echo No se encontro mvn para compilar %~1. & exit /b 1)
echo Compilando %~1 ...
pushd "%PROY%"
call mvn -q package -DskipTests
set "RESULTADO=%ERRORLEVEL%"
popd
exit /b %RESULTADO%

REM La web JSP necesita Payara: se publica en un contenedor propio (puerto 8180, el 8080 es del servidor)
:web_java
where docker >nul 2>nul || (echo No se encontro Docker, necesario para la web JSP. & exit /b 1)
docker info >nul 2>nul || (echo Docker no esta encendido. Abra Docker Desktop y vuelva a intentar. & exit /b 1)
set "DESPLIEGUE=%LOCALAPPDATA%\EurekaBankGR02\payara_clientes"
if not exist "%DESPLIEGUE%" mkdir "%DESPLIEGUE%"
copy /y "%BASE%\04. CLIWEB\CLIWEB_EurekaBank_%SUFIJO%\target\CLIWEB_EurekaBank_%SUFIJO%.war" "%DESPLIEGUE%\" >nul
docker rm -f eurekabank-clientes-web >nul 2>nul
echo Publicando la web en Payara (la primera vez tarda en descargar la imagen) ...
docker run -d --rm --name eurekabank-clientes-web -p %PUERTO%:8080 -v "%DESPLIEGUE%:/opt/payara/deployments" payara/server-full:6.2024.6-jdk17 >nul
exit /b %ERRORLEVEL%

REM ------------------------------------------------------------------ utilidades
REM Espera hasta 3 minutos a que responda la web y la abre en el navegador
:esperar_web
echo Esperando la web en %URLWEB% ...
set /a INTENTOS=0
:esperar_otra
curl -s -f -o nul "%URLWEB%" && goto web_lista
set /a INTENTOS+=1
if %INTENTOS% geq 60 (echo La web no respondio. Revise su ventana. & exit /b 0)
timeout /t 3 /nobreak >nul
goto esperar_otra
:web_lista
start "" "%URLWEB%"
exit /b 0

:buscar_celular
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB%" set "ADB=%ProgramFiles(x86)%\Android\android-sdk\platform-tools\adb.exe"
set "CELULAR="
if not exist "%ADB%" exit /b 0
for /f "skip=1 tokens=2" %%a in ('call "%ADB%" devices') do if "%%a"=="device" set "CELULAR=1"
exit /b 0

:movil_dotnet
call :buscar_celular
if not defined CELULAR (echo Movil: no hay celular ni emulador conectado, se omite. & exit /b 0)
echo Movil: compilando e instalando en una ventana aparte (tarda varios minutos) ...
start "EurekaBank TI %TI% - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k dotnet build -t:Run -f net10.0-android
exit /b 0

:movil_java
call :buscar_celular
if not defined CELULAR (echo Movil: no hay celular ni emulador conectado, se omite. & exit /b 0)
echo Movil: compilando e instalando en una ventana aparte (tarda varios minutos) ...
start "EurekaBank TI 1.3 - Movil" /D "%BASE%\04. CLIMOV\CLIMOV_EurekaBank_%SUFIJO%" cmd /k "gradlew.bat installDebug && "%ADB%" shell monkey -p ec.edu.monster.eurekabank.restjava -c android.intent.category.LAUNCHER 1"
exit /b 0

:error
echo No se pudieron levantar los clientes. Revise el mensaje de arriba.
pause
exit /b 1

:fin
echo.
echo Listo. Usuario de prueba: MONSTER / Monster9
echo Web: %URLWEB%
if "%TI%"=="1.3" echo Para apagar la web: docker stop eurekabank-clientes-web
echo Para cerrar los demas clientes, cierre sus ventanas.
echo El servidor debe estar encendido en la direccion que aparece en cada login.
if "%~1"=="" pause
endlocal
