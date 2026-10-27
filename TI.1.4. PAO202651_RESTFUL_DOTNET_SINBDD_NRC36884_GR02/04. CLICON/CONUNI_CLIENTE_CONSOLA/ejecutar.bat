@echo off
REM La consola clasica de Windows 10 no tiene braille ni emojis: si hay
REM Windows Terminal y no estamos ya dentro de el, se vuelve a abrir ahi
if not defined WT_SESSION (
    where wt > nul 2> nul && (
        start "" wt -d "%~dp0." cmd /c "%~f0"
        exit /b
    )
)

REM Consola en UTF-8 para que se vean los emojis y el arte de Sullivan
chcp 65001 > nul < nul
cd /d "%~dp0"

if not exist "target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar" (
    echo Compilando el proyecto...
    call mvn -q package
)

REM Para usar el servidor en la misma PC:
REM java -Dconuni.url=http://localhost:5014/api/conversor -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar
java -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar
pause
