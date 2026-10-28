# Script PowerShell para ejecutar el cliente RESTful Monsters Inc
# Configuración completa de codificación UTF-8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MONSTERS INC. CONVERTER RESTFUL" -ForegroundColor Cyan
Write-Host "Configurando codificación UTF-8..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar codificación de consola
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8

# Configurar variables de entorno
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
$env:JAVA_OPTS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

Write-Host "Codificación configurada correctamente" -ForegroundColor Green
Write-Host ""

# Ejecutar Maven
Write-Host "Ejecutando cliente RESTful..." -ForegroundColor Yellow
Write-Host ""

try {
    mvn exec:java -Dexec.mainClass="ec.edu.monster.prueba.ClientePrincipal" -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8
} catch {
    Write-Host "Error al ejecutar el cliente: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Presione cualquier tecla para salir..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")


