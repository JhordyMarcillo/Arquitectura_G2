@echo off
cd /d "%~dp0"

if not exist "target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar" (
    echo Compilando el proyecto...
    call mvn -q package
)

REM Para usar el servidor en la misma PC:
REM start "" javaw -Dconuni.url=http://localhost:5014/api/conversor -jar target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar
start "" javaw -jar target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar
