# Menu de TG.1.1 (EurekaBank SOAP Java con MySQL en Amazon RDS).
# Uso directo: menu.ps1 -Accion ip|login|servidor|consola|escritorio|web|movil|detener  [-Ip 192.168.1.20]
param([string]$Accion = '', [string]$Ip = 'localhost')
$ErrorActionPreference = 'Continue'
$Raiz = Split-Path $PSScriptRoot -Parent
$Sufijo = 'EurekaBank_JAVA_SOAP_NUBE_GR02'
$Region = 'us-east-2'
$GrupoSeguridad = 'gpcaceres-rds-sg'

$aws = 'C:\Program Files\Amazon\AWSCLIV2\aws.exe'
if (-not (Test-Path $aws)) { $aws = 'aws' }
$jdk = Get-ChildItem 'C:\Program Files\Eclipse Adoptium' -Directory -Filter 'jdk-21*' -ErrorAction SilentlyContinue | Select-Object -First 1
if ($jdk) { $env:JAVA_HOME = $jdk.FullName; $env:Path = "$($jdk.FullName)\bin;$env:Path" }
$mvn = (Get-Command mvn.cmd -ErrorAction SilentlyContinue).Source
if (-not $mvn) { $mvn = 'C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd' }
$asadmin = @($env:PAYARA_HOME, "$env:USERPROFILE\Payara_Server", "$env:USERPROFILE\Documents\payara7", 'C:\payara7') |
    Where-Object { $_ -and (Test-Path "$_\bin\asadmin.bat") } | Select-Object -First 1 | ForEach-Object { "$_\bin\asadmin.bat" }
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

function Url { "http://${Ip}:8080/WS_$Sufijo/WSEurekaBank" }
function Carpeta($tipo, $prefijo) { Join-Path $Raiz "$tipo\${prefijo}_$Sufijo" }

function Compilar($carpeta) {
    Write-Host "Compilando $(Split-Path $carpeta -Leaf) ..."
    Push-Location -LiteralPath $carpeta
    & $mvn -q -DskipTests package | Out-Host
    $ok = $LASTEXITCODE -eq 0
    Pop-Location
    if (-not $ok) { Write-Host 'No se pudo compilar.' -ForegroundColor Red }
    return $ok
}

function Jar($carpeta) {
    Get-ChildItem -LiteralPath "$carpeta\target" -Filter *.jar -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch '^original-' } | Sort-Object Length -Descending | Select-Object -First 1
}

function Actualizar-Ip {
    & $aws sts get-caller-identity --output text 2>$null | Out-Null
    if ($LASTEXITCODE -ne 0) { Write-Host 'La sesion de AWS vencio. Use la opcion 2 (Login AWS).' -ForegroundColor Yellow; return }
    $miIp = (Invoke-RestMethod 'https://checkip.amazonaws.com').Trim()
    $sg = & $aws ec2 describe-security-groups --region $Region --filters "Name=group-name,Values=$GrupoSeguridad" --query 'SecurityGroups[0].GroupId' --output text
    $r = & $aws ec2 authorize-security-group-ingress --region $Region --group-id $sg --protocol tcp --port 3306 --cidr "$miIp/32" 2>&1
    if ($LASTEXITCODE -eq 0) { Write-Host "Acceso agregado para $miIp" }
    elseif ("$r" -match 'Duplicate') { Write-Host "La IP $miIp ya tenia acceso" }
    else { Write-Host "$r" -ForegroundColor Red }
}

function Encender-Payara {
    if (-not $asadmin) { Write-Host 'No se encontro Payara (defina PAYARA_HOME).' -ForegroundColor Red; return $false }
    $archivo = Join-Path $Raiz '03. BDD\.env'
    if (-not (Test-Path -LiteralPath $archivo)) { Write-Host 'Falta "03. BDD\.env": ejecute cargar_bdd_nube.ps1.' -ForegroundColor Red; return $false }
    # Payara recibe la conexion por variables de entorno: hay que reiniciarlo para que las lea
    Get-Content -LiteralPath $archivo | Where-Object { $_ -match '^(DB_\w+)=(.*)$' } | ForEach-Object { Set-Item "Env:$($matches[1])" $matches[2] }
    & $asadmin --interactive=false stop-domain domain1 2>&1 | Out-Null
    Start-Sleep -Seconds 2
    $ocupado = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($ocupado) {
        $prog = (Get-Process -Id $ocupado.OwningProcess -ErrorAction SilentlyContinue).Name
        Write-Host "El puerto 8080 esta ocupado por '$prog'. Cierrelo y vuelva a intentar." -ForegroundColor Red
        return $false
    }
    # Sin tuberia: el dominio hereda la salida y la dejaria abierta
    Write-Host 'Encendiendo Payara ...'
    # Payara responde antes de cargar sus aplicaciones; desplegar en ese momento las deja en error.
    # El mensaje NCLS-JMX-00005 del registro indica que ya termino.
    $registro = Join-Path (Split-Path (Split-Path $asadmin)) 'glassfish\domains\domain1\logs\server.log'
    $desde = Get-Date
    Start-Process $asadmin -ArgumentList '--interactive=false', 'start-domain', 'domain1' -WindowStyle Hidden
    for ($i = 0; $i -lt 60; $i++) {
        Start-Sleep -Seconds 3
        $ultima = Select-String -LiteralPath $registro -Pattern '^\[([\dT:.-]{23}).*NCLS-JMX-00005' -ErrorAction SilentlyContinue | Select-Object -Last 1
        if ($ultima -and [datetime]$ultima.Matches[0].Groups[1].Value -gt $desde) { Start-Sleep -Seconds 3; return $true }
    }
    Write-Host 'Payara no termino de encender.' -ForegroundColor Red
    return $false
}

function Desplegar($carpeta, $nombre) {
    $war = Get-ChildItem -LiteralPath "$carpeta\target" -Filter *.war | Select-Object -First 1
    & $asadmin --interactive=false deploy --force=true --name $nombre --contextroot $nombre $war.FullName 2>&1 | Select-Object -Last 1 | Out-Host
}

function Servidor {
    $c = Carpeta '01. SERVIDOR' 'WS'
    if (-not (Compilar $c)) { return }
    if (-not (Encender-Payara)) { return }
    Desplegar $c "WS_$Sufijo"
    try { $r = Invoke-WebRequest -UseBasicParsing "http://localhost:8080/WS_$Sufijo/WSEurekaBank?wsdl" -TimeoutSec 40; Write-Host "Servidor listo (WSDL HTTP $($r.StatusCode))" -ForegroundColor Green }
    catch { Write-Host "El servidor no respondio: $($_.Exception.Message)" -ForegroundColor Red }
    $mia = (Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway } | Select-Object -First 1).IPv4Address.IPAddress
    Write-Host "IP de este equipo para los clientes: $mia"
}

function Consola {
    $c = Carpeta '02. CLICON' 'CLICON'
    if (Compilar $c) { Start-Process cmd -ArgumentList '/k', "chcp 65001 >nul & java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -jar `"$((Jar $c).FullName)`" $(Url)" }
}

function Escritorio {
    $c = Carpeta '02. CLIESC' 'CLIESC'
    if (Compilar $c) { Start-Process javaw -ArgumentList '-jar', "`"$((Jar $c).FullName)`"", (Url) }
}

function Web {
    $c = Carpeta '02. CLIWEB' 'CLIWEB'
    if (Compilar $c) { Desplegar $c "CLIWEB_$Sufijo"; Start-Process "http://localhost:8080/CLIWEB_$Sufijo" }
}

function Movil {
    $cel = & $adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\sdevice$' } | ForEach-Object { ($_ -split '\s')[0] } | Select-Object -First 1
    if (-not $cel) { Write-Host 'No hay celular ni emulador conectado.' -ForegroundColor Yellow; return }
    $destino = $Ip
    if ($destino -eq 'localhost') {
        $destino = if ($cel -like 'emulator-*') { '10.0.2.2' } else { (Get-NetIPConfiguration | Where-Object { $_.IPv4DefaultGateway } | Select-Object -First 1).IPv4Address.IPAddress }
    }
    Remove-Item Env:ANDROID_SDK_ROOT -ErrorAction SilentlyContinue
    $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
    Push-Location -LiteralPath (Carpeta '02. CLIMOV' 'CLIMOV')
    & .\gradlew.bat installDebug --console=plain | Select-Object -Last 3 | Out-Host
    Pop-Location
    & $adb -s $cel shell am start -S -n ec.edu.monster.eurekabank.soapjavanube/ec.edu.monster.vistas.ActividadLogin --es urlServidor "http://${destino}:8080/WS_$Sufijo/WSEurekaBank" | Out-Null
    Write-Host "Aplicacion abierta en $cel (servidor $destino)"
}

function Detener {
    foreach ($n in "CLIWEB_$Sufijo", "WS_$Sufijo") { & $asadmin --interactive=false undeploy $n 2>&1 | Select-Object -Last 1 | Out-Host }
}

function Ejecutar($a) {
    switch ($a) {
        { $_ -in '1', 'ip' } { Actualizar-Ip }
        { $_ -in '2', 'login' } { & $aws login --region $Region }
        { $_ -in '3', 'servidor' } { Servidor }
        { $_ -in '4', 'consola' } { Consola }
        { $_ -in '5', 'escritorio' } { Escritorio }
        { $_ -in '6', 'web' } { Web }
        { $_ -in '7', 'movil' } { Movil }
        { $_ -in '8', 'detener' } { Detener }
    }
}

if ($Accion) { Ejecutar $Accion; exit }
while ($true) {
    Clear-Host
    Write-Host ''
    Write-Host '  =================================================='
    Write-Host '    TG.1.1  EUREKABANK SOAP JAVA - BDD EN LA NUBE'
    Write-Host '    MySQL en Amazon RDS (us-east-2)  |  GR02'
    Write-Host '  =================================================='
    Write-Host '    1. Actualizar IP en AWS'
    Write-Host '    2. Login AWS'
    Write-Host '    3. Levantar servidor'
    Write-Host '    4. Cliente consola'
    Write-Host '    5. Cliente escritorio'
    Write-Host '    6. Cliente web'
    Write-Host '    7. Cliente movil'
    Write-Host '    8. Detener servidor y web'
    Write-Host '    9. Salir'
    $o = (Read-Host '  Elige una opcion (1-9)').Trim()
    if ($o -eq '9') { exit }
    if ($o -in '4', '5', '7') { $x = (Read-Host "  IP del servidor (Enter = $Ip)").Trim(); if ($x) { $Ip = $x } }
    Ejecutar $o
    Read-Host '  Enter para volver al menu' | Out-Null
}
