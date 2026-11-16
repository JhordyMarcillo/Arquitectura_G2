# Levanta la TI.1.1 (SOAP Java + MySQL, EurekaBank) para la presentacion:
# MySQL + servidor en Payara + cliente web + consola + escritorio + movil (Android).
# La IP y el modo del celular se toman de ..\CONFIGURACION_PRESENTACION.txt
# Uso:  powershell -ExecutionPolicy Bypass -File levantar.ps1
#       powershell -ExecutionPolicy Bypass -File levantar.ps1 -Detener
#       powershell -ExecutionPolicy Bypass -File levantar.ps1 -RestaurarBD
param(
    [switch]$Detener,
    [switch]$RestaurarBD,
    [switch]$Prueba,   # solo para pruebas: sin administrador, sin abrir ventanas, navegador ni celular
    [switch]$Elevado   # interno: la copia ya elevada nunca vuelve a relanzarse
)

$ErrorActionPreference = 'Continue'
$Raiz = Split-Path $PSScriptRoot -Parent

# ------------------------------------------------------------------ administrador
# Hace falta para abrir el firewall y arrancar el servicio de MySQL.
$esAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $esAdmin -and -not $Prueba -and -not $Elevado -and -not $RestaurarBD) {
    $argumentos = @('-NoExit', '-ExecutionPolicy', 'Bypass', '-File', "`"$PSCommandPath`"", '-Elevado')
    if ($Detener) { $argumentos += '-Detener' }
    Start-Process powershell -Verb RunAs -ArgumentList $argumentos
    exit
}

function Paso($texto) { Write-Host ''; Write-Host "==> $texto" -ForegroundColor Cyan }
function Ok($texto) { Write-Host "    OK  $texto" -ForegroundColor Green }
function Aviso($texto) { Write-Host "    !!  $texto" -ForegroundColor Yellow }
function Falla($texto) { Write-Host "    XX  $texto" -ForegroundColor Red; $script:fallas += $texto }
$script:fallas = @()

$Prefijo = 'TI 1.1 BDD'
$PidDir = Join-Path $env:TEMP 'presentacion_gr02'
New-Item -ItemType Directory -Force $PidDir | Out-Null

function Guardar-Pid($titulo, $id) {
    Set-Content (Join-Path $PidDir (($titulo -replace '[^\w]', '_') + '.pid')) $id
}
function Detener-Ventanas {
    $filtro = ($Prefijo -replace '[^\w]', '_') + '*.pid'
    Get-ChildItem $PidDir -Filter $filtro -ErrorAction SilentlyContinue | ForEach-Object {
        Get-Content $_.FullName | ForEach-Object { taskkill /F /T /PID $_ 2>$null | Out-Null }
        Remove-Item $_.FullName -Force
    }
    Get-Process cmd, powershell, conhost -ErrorAction SilentlyContinue |
        Where-Object { $_.MainWindowTitle -like "$Prefijo*" -and $_.Id -ne $PID } |
        ForEach-Object { taskkill /F /T /PID $_.Id 2>$null | Out-Null }
}

# ------------------------------------------------------------------ herramientas
$cfg = @{}
Get-Content (Join-Path $Raiz 'CONFIGURACION_PRESENTACION.txt') | ForEach-Object {
    $l = ($_ -replace '#.*$', '').Trim()
    if ($l -match '^\s*(\w+)\s*=\s*(.+)$') { $cfg[$matches[1].ToUpper()] = $matches[2].Trim() }
}

function Primero([string[]]$rutas) { $rutas | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1 }

$mysql = Get-ChildItem 'C:\Program Files\MySQL\*\bin\mysql.exe' -ErrorAction SilentlyContinue | Sort-Object FullName -Descending | Select-Object -First 1 -ExpandProperty FullName
$PayaraHome = $cfg['PAYARA']
if (-not $PayaraHome -or $PayaraHome -eq 'AUTO') {
    $PayaraHome = Primero @($env:PAYARA_HOME, "$env:USERPROFILE\Payara_Server", "$env:USERPROFILE\Documents\payara7",
                            "$env:USERPROFILE\payara7", 'C:\payara7', 'C:\Payara_Server')
}
$asadmin = if ($PayaraHome) { Join-Path $PayaraHome 'bin\asadmin.bat' } else { $null }
$mvn = (Get-Command mvn.cmd -ErrorAction SilentlyContinue).Source
if (-not $mvn) { $mvn = Primero @('C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd') }
$java = (Get-Command java -ErrorAction SilentlyContinue).Source
$javaw = (Get-Command javaw -ErrorAction SilentlyContinue).Source
$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
# Gradle 8.10 del movil necesita un JDK entre 17 y 21 (el de Android Studio puede ser mas nuevo)
function Version-Jdk($dir) {
    $rel = Join-Path $dir 'release'
    if (Test-Path $rel) { if ((Get-Content $rel -Raw) -match 'JAVA_VERSION="(\d+)') { return [int]$matches[1] } }
    return 0
}
$jdkAndroid = @($env:JAVA_HOME) + @(Get-Item "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-*", 'C:\Program Files\Eclipse Adoptium\jdk-*',
        'C:\Program Files\Microsoft\jdk-*', 'C:\Program Files\Java\jdk-*', 'C:\Program Files\Android\Android Studio\jbr' -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object FullName) |
    Where-Object { $_ -and (Test-Path $_) -and (Version-Jdk $_) -ge 17 -and (Version-Jdk $_) -le 21 } | Select-Object -First 1

$BDD = Join-Path $Raiz '03. BDD'
$Servidor = Join-Path $Raiz '04. SERVIDOR\WS_EurekaBank_JAVA_SOAP_GR02'
$Web = Join-Path $Raiz '04. CLIWEB\CLIWEB_EurekaBank_JAVA_SOAP_GR02'
$Consola = Join-Path $Raiz '04. CLICON\CLICON_EurekaBank_JAVA_SOAP_GR02'
$Escritorio = Join-Path $Raiz '04. CLIESC\CLIESC_EurekaBank_JAVA_SOAP_GR02'
$Movil = Join-Path $Raiz '04. CLIMOV\CLIMOV_EurekaBank_JAVA_SOAP_GR02'
$apkDir = Join-Path $PSScriptRoot 'APKs'
$PaqueteMovil = 'ec.edu.monster.eurekabank.soapjava'
$Ruta = '/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank'

# ------------------------------------------------------------------ detener
if ($Detener) {
    Paso 'Cerrando las ventanas y clientes de la TI.1.1'
    Detener-Ventanas
    Ok 'Listo (Payara y MySQL siguen encendidos; Payara se apaga con: asadmin stop-domain)'
    return
}

# ------------------------------------------------------------------ restaurar la base
if ($RestaurarBD) {
    Paso 'Restaurando la base eurekabank (Crea_BD.sql + Carga_Datos.sql)'
    if (-not $mysql) { Falla 'No se encontro mysql.exe en C:\Program Files\MySQL'; return }
    Write-Host '    Se borra la base y se vuelve a crear con los datos iniciales.' -ForegroundColor Yellow
    Write-Host '    Escribe la clave de root de MySQL cuando la pida:' -ForegroundColor Yellow
    Push-Location $BDD
    & $mysql -u root -p --default-character-set=utf8mb4 -e 'source Crea_BD.sql; source Carga_Datos.sql;'
    $codigo = $LASTEXITCODE
    Pop-Location
    if ($codigo -eq 0) { Ok 'Base restaurada' } else { Falla 'No se pudo restaurar (revisa la clave de root)' }
    return
}

# ------------------------------------------------------------------ IP
$IP = $cfg['IP']
if (-not $IP -or $IP -eq 'AUTO') {
    $red = Get-NetIPConfiguration | Where-Object {
        $_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up' -and
        $_.InterfaceAlias -notmatch 'vEthernet|VMware|VirtualBox|Loopback|Bluetooth'
    } | Select-Object -First 1
    if ($red) { $IP = $red.IPv4Address[0].IPAddress; $origenIp = "detectada automaticamente ($($red.InterfaceAlias))" }
    else { $IP = '127.0.0.1'; $origenIp = 'sin red activa' }
} else { $origenIp = 'fijada en CONFIGURACION_PRESENTACION.txt' }
$modoCel = 'RED'
if ($cfg['CELULAR']) { $modoCel = $cfg['CELULAR'].ToUpper() }
$UrlServicio = "http://${IP}:8080$Ruta"

$Host.UI.RawUI.WindowTitle = "$Prefijo - lanzador"
Write-Host ''
Write-Host '=================================================================' -ForegroundColor Magenta
Write-Host "  PRESENTACION TI.1.1 SOAP JAVA CON BDD   IP: $IP  ($origenIp)" -ForegroundColor Magenta
Write-Host "  Celular: $modoCel" -ForegroundColor Magenta
Write-Host '=================================================================' -ForegroundColor Magenta
if ($IP -eq '127.0.0.1') { Aviso 'No hay red activa: solo funcionara en esta PC (y el celular por USB).' }

foreach ($par in @(@('Payara (bin\asadmin.bat)', $asadmin), @('Maven (mvn)', $mvn), @('Java', $java))) {
    if (-not $par[1] -or -not (Test-Path $par[1])) { Falla "No se encontro $($par[0])" }
}
if ($script:fallas.Count) { Aviso 'Instala lo que falta (o escribe PAYARA= en CONFIGURACION_PRESENTACION.txt) y vuelve a ejecutar.'; return }

# ------------------------------------------------------------------ utilidades
function Maven($dir, $metas) {
    Push-Location $dir
    $salida = cmd /c "`"$mvn`" -q -B $metas 2>&1"
    $codigo = $LASTEXITCODE
    Pop-Location
    if ($codigo -ne 0) { $salida | Select-String 'ERROR' | Select-Object -First 5 | Out-Host }
    return ($codigo -eq 0)
}
function EsperarUrl($url, $segundos) {
    $fin = (Get-Date).AddSeconds($segundos)
    while ((Get-Date) -lt $fin) {
        try { $r = Invoke-WebRequest $url -UseBasicParsing -TimeoutSec 5; if ($r.StatusCode -lt 500) { return $true } } catch { Start-Sleep 2 }
    }
    return $false
}
function EsperarPuerto($puerto, $segundos) {
    $fin = (Get-Date).AddSeconds($segundos)
    while ((Get-Date) -lt $fin) {
        if (Get-NetTCPConnection -LocalPort $puerto -State Listen -ErrorAction SilentlyContinue) { return $true }
        Start-Sleep 2
    }
    return $false
}
# Abre una ventana de consola con titulo propio que ejecuta los comandos dados.
function Ventana($titulo, $dir, [string[]]$comandos) {
    $cmd = Join-Path $env:TEMP ('pres_' + ($titulo -replace '[^\w]', '_') + '.cmd')
    $contenido = @('@echo off', 'chcp 65001 >nul', "title $titulo", "cd /d `"$dir`"") + $comandos
    [IO.File]::WriteAllLines($cmd, $contenido, (New-Object Text.UTF8Encoding($false)))
    $proc = Start-Process cmd -ArgumentList '/k', "`"$cmd`"" -PassThru
    Guardar-Pid $titulo $proc.Id
}
function Desplegar($war, $nombre) {
    $salida = & $asadmin deploy --force=true --name $nombre --contextroot $nombre "$war" 2>&1 | Out-String
    if ($salida -match 'executed successfully') { Ok "$nombre desplegado" } else { Falla "No se desplego $nombre`: $($salida.Trim())" }
}

if (-not $Prueba) { Detener-Ventanas }

# ------------------------------------------------------------------ 1. MySQL
Paso 'Base de datos MySQL'
if (-not (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue)) {
    $servicio = Get-Service -ErrorAction SilentlyContinue | Where-Object { $_.Name -like 'MySQL*' } | Select-Object -First 1
    if ($servicio) {
        try { Start-Service $servicio.Name -ErrorAction Stop; Ok "Servicio $($servicio.Name) iniciado" }
        catch { Falla "No se pudo iniciar el servicio $($servicio.Name) (hace falta administrador)" }
    } elseif (Get-Command docker -ErrorAction SilentlyContinue) {
        Push-Location $BDD; docker compose up -d 2>&1 | Select-Object -Last 2 | Out-Host; Pop-Location
    } else { Falla 'MySQL no esta encendido y no se encontro su servicio ni Docker' }
    if (-not (EsperarPuerto 3306 60)) { Falla 'MySQL no responde en el puerto 3306' }
} else { Ok 'MySQL escuchando en el puerto 3306' }

# Comprueba la base con el mismo usuario que usa el servidor (db.properties)
$props = @{}
Get-Content (Join-Path $Servidor 'src\main\resources\db.properties') | ForEach-Object {
    if ($_ -match '^\s*([\w.]+)\s*=\s*(.*)$') { $props[$matches[1]] = $matches[2].Trim() }
}
if ($mysql) {
    $env:MYSQL_PWD = $props['db.password']
    $cuentas = & $mysql -u $props['db.user'] -N -B eurekabank -e 'SELECT COUNT(*) FROM Cuenta' 2>&1
    Remove-Item Env:MYSQL_PWD
    if ($LASTEXITCODE -eq 0) { Ok "Base eurekabank lista ($($cuentas.Trim()) cuentas)" }
    else { Falla 'La base eurekabank no existe o el usuario eureka no entra: ejecuta RESTAURAR_BD_TI_1.1.bat' }
} else { Aviso 'No se encontro mysql.exe: no se pudo comprobar la base' }

# ------------------------------------------------------------------ 2. Firewall
Paso 'Firewall'
if ($esAdmin) {
    $nombre = 'Presentacion GR02 8080'
    if (-not (Get-NetFirewallRule -DisplayName $nombre -ErrorAction SilentlyContinue)) {
        New-NetFirewallRule -DisplayName $nombre -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Any | Out-Null
    }
    Ok 'Firewall abierto para el puerto 8080'
} else { Aviso 'Sin permisos de administrador: no se abrio el firewall (solo esta PC podra conectarse).' }

# ------------------------------------------------------------------ 3. Servidor y cliente web en Payara
Paso 'Payara Server'
if (-not (Get-NetTCPConnection -LocalPort 4848 -State Listen -ErrorAction SilentlyContinue)) {
    & $asadmin start-domain 2>&1 | Select-Object -Last 1 | Out-Host
}
if (EsperarPuerto 8080 90) { Ok "Payara encendido ($PayaraHome)" } else { Falla 'Payara no arranco' }

Paso 'Servidor SOAP (WS_EurekaBank_JAVA_SOAP_GR02)'
if (Maven $Servidor 'clean package -DskipTests') {
    Desplegar (Join-Path $Servidor 'target\WS_EurekaBank_JAVA_SOAP_GR02.war') 'WS_EurekaBank_JAVA_SOAP_GR02'
} else { Falla 'No compilo el servidor' }
if (EsperarUrl "http://localhost:8080$Ruta`?wsdl" 60) { Ok 'WSDL respondiendo' } else { Falla 'El servicio no responde' }

Paso 'Cliente web (JSP + Servlets en el mismo Payara)'
if (Maven $Web 'clean package') {
    Desplegar (Join-Path $Web 'target\CLIWEB_EurekaBank_JAVA_SOAP_GR02.war') 'CLIWEB_EurekaBank_JAVA_SOAP_GR02'
} else { Falla 'No compilo el cliente web' }
$UrlWeb = "http://${IP}:8080/CLIWEB_EurekaBank_JAVA_SOAP_GR02/"
if (EsperarUrl 'http://localhost:8080/CLIWEB_EurekaBank_JAVA_SOAP_GR02/' 60) {
    Ok 'Web lista'
    if (-not $Prueba) { Start-Process $UrlWeb }
} else { Falla 'La web no responde' }

# ------------------------------------------------------------------ 4. Consola
Paso 'Cliente consola (Java)'
if (Maven $Consola 'clean package') {
    if ($Prueba) { Ok 'Consola compilada' }
    else { Ventana "$Prefijo - Consola" $Consola @("`"$java`" -jar target\CLICON_EurekaBank_JAVA_SOAP_GR02.jar $UrlServicio"); Ok 'Ventana de consola abierta' }
} else { Falla 'No compilo la consola' }

# ------------------------------------------------------------------ 5. Escritorio
Paso 'Cliente escritorio (Java Swing)'
if (Maven $Escritorio 'clean package') {
    if ($Prueba) { Ok 'Escritorio compilado' }
    else {
        $proc = Start-Process $javaw -ArgumentList '-jar', "`"$Escritorio\target\CLIESC_EurekaBank_JAVA_SOAP_GR02.jar`"", $UrlServicio -PassThru
        Guardar-Pid "$Prefijo - Escritorio" $proc.Id
        Ok 'Escritorio abierto'
    }
} else { Falla 'No compilo el escritorio' }

# ------------------------------------------------------------------ 6. Movil
Paso 'Cliente movil (Android)'
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
if ($jdkAndroid) { $env:JAVA_HOME = $jdkAndroid; Ok "JDK para Gradle: $jdkAndroid" } else { Aviso 'No se encontro un JDK 17-21 para Gradle' }
# El proyecto compila en una ruta corta (build.gradle.kts): %LOCALAPPDATA%\EurekaBankGR02
$apk = Join-Path $env:LOCALAPPDATA 'EurekaBankGR02\CLIMOV_EurekaBank_JAVA_SOAP_GR02\app\outputs\apk\debug\app-debug.apk'
Remove-Item $apk -ErrorAction SilentlyContinue
cmd /c "cd /d `"$Movil`" && .\gradlew.bat assembleDebug -q --console=plain 2>&1" |
    Select-String -Pattern 'FAILED|error:|wrong' | Select-Object -First 5 | Out-Host
if (Test-Path $apk) {
    New-Item -ItemType Directory -Force $apkDir | Out-Null
    Copy-Item $apk (Join-Path $apkDir 'TI_1.1_BDD.apk') -Force
    Ok 'APK listo en presentacion\APKs\TI_1.1_BDD.apk'
    if ($Prueba) {
        # nada
    } elseif (-not (Test-Path $adb)) {
        Aviso "No se encontro adb: instala el APK a mano y escribe en el login: $UrlServicio"
    } else {
        $equipos = & $adb devices | Select-String '^(\S+)\tdevice$' | ForEach-Object { $_.Matches[0].Groups[1].Value }
        if (-not $equipos) {
            Aviso 'No hay celular ni emulador conectado (o falta aceptar la depuracion USB).'
            Aviso "Instala el APK a mano y escribe en el login: $UrlServicio"
        }
        foreach ($e in $equipos) {
            if ($e -like 'emulator-*') { $urlMovil = "http://10.0.2.2:8080$Ruta" }
            elseif ($modoCel -eq 'USB') { & $adb -s $e reverse tcp:8080 tcp:8080 | Out-Null; $urlMovil = "http://127.0.0.1:8080$Ruta" }
            else { $urlMovil = $UrlServicio }
            # --user 0: solo el perfil principal (evita el icono duplicado en Samsung)
            $salida = & $adb -s $e install -r -t --user 0 "$apk" 2>&1 | Out-String
            if ($salida -notmatch 'Success') { $salida = & $adb -s $e install -r -t "$apk" 2>&1 | Out-String }
            if ($salida -match 'Success') {
                & $adb -s $e shell am start -S -n "$PaqueteMovil/ec.edu.monster.vistas.ActividadLogin" --es urlServidor $urlMovil 2>&1 | Out-Null
                Ok "App instalada y abierta en $e (usa $urlMovil)"
            } else { Falla "No se pudo instalar la app en $e`: $($salida.Trim())" }
        }
    }
} else { Falla 'No se genero el APK' }

# ------------------------------------------------------------------ resumen
Write-Host ''
Write-Host '=================================================================' -ForegroundColor Magenta
Write-Host "  TI.1.1 LISTO   IP: $IP" -ForegroundColor Magenta
Write-Host '  Usuario / clave: MONSTER / Monster9    Clave de las cuentas: 123456' -ForegroundColor Magenta
Write-Host "  Servicio (WSDL): $UrlServicio`?wsdl" -ForegroundColor Magenta
Write-Host "  Web:             $UrlWeb" -ForegroundColor Magenta
Write-Host '  Desde el celular (navegador) tambien se abre la web con la IP de arriba.' -ForegroundColor Magenta
Write-Host '=================================================================' -ForegroundColor Magenta
if ($script:fallas.Count) {
    Write-Host 'Revisar:' -ForegroundColor Red
    $script:fallas | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
}
