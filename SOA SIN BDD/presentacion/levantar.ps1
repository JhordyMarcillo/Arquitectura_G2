# Levanta un TI completo para la presentacion: servidor + consola + escritorio + web + movil (Android por USB).
# La IP se toma de ..\CONFIGURACION_PRESENTACION.txt y se escribe en la configuracion de cada cliente.
# Uso:  powershell -ExecutionPolicy Bypass -File levantar.ps1 -TI 1.2
#       powershell -ExecutionPolicy Bypass -File levantar.ps1 -Detener
param(
    [ValidateSet('1.1', '1.2', '1.3', '1.4')][string]$TI,
    [switch]$Detener,
    [switch]$SinAdmin,  # solo para pruebas: no pide elevacion y omite el firewall
    [switch]$Elevado    # interno: la copia ya elevada nunca vuelve a relanzarse
)

$ErrorActionPreference = 'Continue'
$Raiz = Split-Path $PSScriptRoot -Parent

# ------------------------------------------------------------------ administrador
$esAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $esAdmin -and -not $SinAdmin -and -not $Elevado) {
    $argumentos = @('-NoExit', '-ExecutionPolicy', 'Bypass', '-File', "`"$PSCommandPath`"", '-Elevado')
    if ($Detener) { $argumentos += '-Detener' } else { $argumentos += @('-TI', $TI) }
    Start-Process powershell -Verb RunAs -ArgumentList $argumentos
    exit
}

function Paso($texto) { Write-Host ''; Write-Host "==> $texto" -ForegroundColor Cyan }
function Ok($texto) { Write-Host "    OK  $texto" -ForegroundColor Green }
function Aviso($texto) { Write-Host "    !!  $texto" -ForegroundColor Yellow }
function Falla($texto) { Write-Host "    XX  $texto" -ForegroundColor Red; $script:fallas += $texto }
$script:fallas = @()

# ------------------------------------------------------------------ detener
$PidDir = Join-Path $env:TEMP 'presentacion_gr02'
New-Item -ItemType Directory -Force $PidDir | Out-Null
function Detener-Ventanas($prefijo) {
    $filtro = ($prefijo -replace '[^\w]', '_') + '*.pid'
    Get-ChildItem $PidDir -Filter $filtro -ErrorAction SilentlyContinue | ForEach-Object {
        Get-Content $_.FullName | ForEach-Object { taskkill /F /T /PID $_ 2>$null | Out-Null }
        Remove-Item $_.FullName -Force
    }
    Get-Process cmd, powershell, conhost -ErrorAction SilentlyContinue |
        Where-Object { $_.MainWindowTitle -like "$prefijo*" -and $_.Id -ne $PID } |
        ForEach-Object { taskkill /F /T /PID $_.Id 2>$null | Out-Null }
}
function Liberar-Puerto($puerto) {
    Get-NetTCPConnection -LocalPort $puerto -State Listen -ErrorAction SilentlyContinue |
        Where-Object { $_.OwningProcess -gt 4 } |
        ForEach-Object { taskkill /F /T /PID $_.OwningProcess 2>$null | Out-Null }
}
if ($Detener) {
    Paso 'Cerrando ventanas y clientes de la presentacion'
    Detener-Ventanas 'TI 1.'
    Get-Process iisexpress, CONUNI_SOAP_DOTNET_CLIESC_G02, SistemaConversorUI.Maui -ErrorAction SilentlyContinue | Stop-Process -Force
    foreach ($p in 5015, 5240, 5014, 3000) { Liberar-Puerto $p }
    Ok 'Listo (Payara sigue encendido; se apaga con: asadmin stop-domain)'
    return
}
if (-not $TI) { Write-Host 'Indica el TI: -TI 1.1 | 1.2 | 1.3 | 1.4'; return }

# ------------------------------------------------------------------ configuracion (IP)
$cfgArchivo = Join-Path $Raiz 'CONFIGURACION_PRESENTACION.txt'
$cfg = @{}
Get-Content $cfgArchivo | ForEach-Object {
    $l = ($_ -replace '#.*$', '').Trim()
    if ($l -match '^\s*(\w+)\s*=\s*(.+)$') { $cfg[$matches[1].ToUpper()] = $matches[2].Trim() }
}
$IP = $cfg['IP']
if (-not $IP -or $IP -eq 'AUTO') {
    $red = Get-NetIPConfiguration | Where-Object {
        $_.IPv4DefaultGateway -and $_.NetAdapter.Status -eq 'Up' -and
        $_.InterfaceAlias -notmatch 'vEthernet|VMware|VirtualBox|Loopback|Bluetooth'
    } | Select-Object -First 1
    if ($red) { $IP = $red.IPv4Address[0].IPAddress } else { $IP = '127.0.0.1' }
    $origenIp = "detectada automaticamente ($($red.InterfaceAlias))"
} else { $origenIp = 'fijada en CONFIGURACION_PRESENTACION.txt' }
$modoCel = 'RED'
if ($cfg['CELULAR']) { $modoCel = $cfg['CELULAR'].ToUpper() }
$IPMovil = $IP
if ($modoCel -eq 'USB') { $IPMovil = '127.0.0.1' }

$Host.UI.RawUI.WindowTitle = "TI $TI - lanzador"
Write-Host ''
Write-Host '=================================================================' -ForegroundColor Magenta
Write-Host "  PRESENTACION TI.$TI   IP del servidor: $IP  ($origenIp)" -ForegroundColor Magenta
Write-Host "  Celular: $modoCel  (el movil usara $IPMovil)" -ForegroundColor Magenta
Write-Host '=================================================================' -ForegroundColor Magenta
if ($IP -eq '127.0.0.1') { Aviso 'No hay red activa: solo funcionara en esta PC (y el celular por USB).' }

# ------------------------------------------------------------------ utilidades
$Payara = Join-Path $env:USERPROFILE 'Documents\payara7'
$asadmin = Join-Path $Payara 'bin\asadmin.bat'
$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
$jbr = 'C:\Program Files\Android\Android Studio\jbr'
$dotnet = 'C:\Program Files\dotnet\dotnet.exe'
$apkDir = Join-Path $Raiz 'presentacion\APKs'
New-Item -ItemType Directory -Force $apkDir | Out-Null
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'

function Carpeta($patron) { (Get-ChildItem $Raiz -Directory -Filter $patron | Select-Object -First 1).FullName }
function Buscar($dir, $nombre) {
    Get-ChildItem $dir -Recurse -Filter $nombre -ErrorAction SilentlyContinue |
        Where-Object { $_.FullName -notmatch '\\(bin|obj|target|build|\.gradle)\\' } | Select-Object -First 1 -ExpandProperty FullName
}

# Reemplaza en un archivo conservando su codificacion (con o sin BOM).
function Fijar($ruta, $patron, $reemplazo, $descripcion) {
    if (-not $ruta -or -not (Test-Path $ruta)) { Falla "No se encontro el archivo para: $descripcion"; return }
    $bytes = [IO.File]::ReadAllBytes($ruta)
    $bom = $bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF
    $texto = [Text.Encoding]::UTF8.GetString($bytes)
    if ($bom) { $texto = $texto.Substring(1) }
    if (-not [regex]::IsMatch($texto, $patron)) { Falla "No se encontro el valor a cambiar en: $descripcion"; return }
    $nuevo = [regex]::Replace($texto, $patron, $reemplazo)
    if ($nuevo -ne $texto) { [IO.File]::WriteAllText($ruta, $nuevo, (New-Object Text.UTF8Encoding($bom))) }
    Ok $descripcion
}

function Firewall($puertos) {
    if (-not $esAdmin) { Aviso 'Sin permisos de administrador: no se abrio el firewall (solo esta PC podra conectarse).'; return }
    foreach ($p in $puertos) {
        $nombre = "Presentacion GR02 $p"
        if (-not (Get-NetFirewallRule -DisplayName $nombre -ErrorAction SilentlyContinue)) {
            New-NetFirewallRule -DisplayName $nombre -Direction Inbound -Protocol TCP -LocalPort $p -Action Allow -Profile Any | Out-Null
        }
    }
    Ok ("Firewall abierto para los puertos " + ($puertos -join ', '))
}

# Abre una ventana de consola con titulo propio que ejecuta los comandos dados.
function Ventana($titulo, $dir, [string[]]$comandos, [switch]$Minimizada) {
    $cmd = Join-Path $env:TEMP ("pres_" + ($titulo -replace '[^\w]', '_') + '.cmd')
    $contenido = @('@echo off', 'chcp 65001 >nul', "title $titulo", "cd /d `"$dir`"") + $comandos
    [IO.File]::WriteAllLines($cmd, $contenido, (New-Object Text.UTF8Encoding($false)))
    $estilo = 'Normal'
    if ($Minimizada) { $estilo = 'Minimized' }
    $proc = Start-Process cmd -ArgumentList '/k', "`"$cmd`"" -WindowStyle $estilo -PassThru
    Set-Content (Join-Path $PidDir (($titulo -replace '[^\w]', '_') + '.pid')) $proc.Id
}

function EsperarPuerto($puerto, $segundos) {
    $fin = (Get-Date).AddSeconds($segundos)
    while ((Get-Date) -lt $fin) {
        try { $c = New-Object Net.Sockets.TcpClient; $c.Connect('127.0.0.1', $puerto); $c.Close(); return $true } catch { Start-Sleep 2 }
    }
    return $false
}
function EsperarUrl($url, $segundos) {
    $fin = (Get-Date).AddSeconds($segundos)
    while ((Get-Date) -lt $fin) {
        try { $r = Invoke-WebRequest $url -UseBasicParsing -TimeoutSec 5; if ($r.StatusCode -lt 500) { return $true } } catch { Start-Sleep 2 }
    }
    return $false
}

function Payara-Encendido {
    if (-not (Get-NetTCPConnection -LocalPort 4848 -State Listen -ErrorAction SilentlyContinue)) {
        & $asadmin start-domain domain1 | Select-Object -Last 1 | Out-Host
    }
}

function Unidad-Corta($ruta) {
    subst Q: /D 2>$null | Out-Null
    subst Q: "$ruta" | Out-Null
    return 'Q:'
}

# Instala y abre la app en el celular conectado por USB. Si no hay celular, deja el APK en presentacion\APKs.
function Celular($apk, $paquete, $puertos) {
    if (-not $apk -or -not (Test-Path $apk)) { Falla 'No se genero el APK'; return }
    $destino = Join-Path $apkDir "TI_$TI.apk"
    Copy-Item $apk $destino -Force
    Ok "APK copiado en presentacion\APKs\TI_$TI.apk"
    if (-not (Test-Path $adb)) { Aviso 'No se encontro adb: instala el APK copiandolo al celular.'; return }
    $equipos = & $adb devices | Select-String '\tdevice$'
    if (-not $equipos) {
        Aviso 'No hay celular conectado por USB (o falta aceptar la depuracion USB en el celular).'
        Aviso "Conectalo y ejecuta otra vez, o instala a mano: $destino"
        return
    }
    if ($modoCel -eq 'USB') {
        foreach ($p in $puertos) { & $adb reverse "tcp:$p" "tcp:$p" | Out-Null }
        Ok ("adb reverse activo para " + ($puertos -join ', ') + ' (el celular usa 127.0.0.1)')
    }
    # --user 0: solo el perfil principal (sin esto, Samsung Dual Messenger / Carpeta segura duplica el icono)
    $salida = & $adb install -r -t --user 0 "$destino" 2>&1 | Out-String
    if ($salida -match 'Success') { Ok "App instalada ($paquete)" } else { Falla "No se pudo instalar la app: $($salida.Trim())"; return }
    & $adb shell monkey -p $paquete -c android.intent.category.LAUNCHER 1 2>&1 | Out-Null
    Ok 'App abierta en el celular'
}

function Gradle-Apk($proyecto, [switch]$Wrapper) {
    $env:JAVA_HOME = $jbr
    $padre = Split-Path $proyecto -Parent
    $u = Unidad-Corta $padre
    $dirCorto = Join-Path "$u\" (Split-Path $proyecto -Leaf)
    Push-Location $dirCorto
    if ($Wrapper) {
        & .\gradlew.bat assembleDebug -q --console=plain 2>&1 | Select-String -Pattern 'FAILED|error:|e: ' | Select-Object -First 5 | Out-Host
    } else {
        $gradle = Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.13-bin\*\gradle-8.13\bin\gradle.bat" | Select-Object -First 1 -ExpandProperty FullName
        & $gradle assembleDebug -q --console=plain 2>&1 | Select-String -Pattern 'FAILED|error:|e: ' | Select-Object -First 5 | Out-Host
    }
    Pop-Location
    subst Q: /D | Out-Null
    return (Join-Path $proyecto 'app\build\outputs\apk\debug\app-debug.apk')
}

$msbuild = & "${env:ProgramFiles(x86)}\Microsoft Visual Studio\Installer\vswhere.exe" -latest -requires Microsoft.Component.MSBuild -find 'MSBuild\**\Bin\MSBuild.exe' | Select-Object -First 1

Detener-Ventanas "TI $TI"

# ================================================================== TI.1.1  SOAP Java
if ($TI -eq '1.1') {
    $T = Carpeta 'TI.1.1*'
    $puertos = 8080, 5240
    $credenciales = 'MONSTER9 / MONSTER9 (entorno JavaSoap)'
    Paso 'Configurando la IP en los clientes'
    Fijar (Buscar $T 'ConstantesRed.cs') '(IP_WEB_SERVICES\s*=\s*")[^"]*(")' "`${1}$IPMovil`${2}" 'Web / escritorio / movil MAUI: ConstantesRed.cs'
    Paso 'Firewall'; Firewall $puertos
    Paso 'Servidor SOAP Java en Payara'
    Payara-Encendido
    Push-Location "$T\04. SERVIDOR"; mvn -q clean package -DskipTests 2>&1 | Select-String 'ERROR' | Select-Object -First 3; Pop-Location
    $war = Get-ChildItem "$T\04. SERVIDOR\target\*.war" | Select-Object -First 1 -ExpandProperty FullName
    & $asadmin deploy --force=true --name WS_ConUni_Java_GR02-1.0-SNAPSHOT --contextroot WS_ConUni_Java_GR02 "$war" | Select-Object -Last 1
    if (EsperarUrl 'http://localhost:8080/WS_ConUni_Java_GR02/WSConversorUnidades?wsdl' 60) { Ok 'Servidor respondiendo' } else { Falla 'El servidor no responde' }
    Paso 'Cliente web (Blazor)'
    Liberar-Puerto 5240
    $web = "$T\04. CLIWEB\SistemaConversorUI.Web\SistemaConversorUI.Web"
    Push-Location $web; & $dotnet build -nologo -v q 2>&1 | Select-String ' error ' | Select-Object -First 3; Pop-Location
    Ventana "TI 1.1 - Web" $web @("`"$dotnet`" run --no-build --urls http://0.0.0.0:5240") -Minimizada
    if (EsperarPuerto 5240 60) { Ok 'Web lista'; Start-Process "http://${IP}:5240" } else { Falla 'La web no arranco' }
    Paso 'Cliente consola (Java)'
    Ventana "TI 1.1 - Consola" "$T\04. CLICON" @("mvn -q compile exec:java -Dexec.mainClass=ec.edu.monster.Main -Dws.base.url=http://${IP}:8080/WS_ConUni_Java_GR02")
    Ok 'Ventana de consola abierta'
    Paso 'Cliente escritorio (MAUI Windows)'
    $maui = "$T\04. CLIMOV\SistemaConversorUI.Maui"
    Get-Process SistemaConversorUI.Maui -ErrorAction SilentlyContinue | Stop-Process -Force
    Push-Location $maui; & $dotnet build -f net10.0-windows10.0.19041.0 -nologo -v q 2>&1 | Select-String ' error ' | Select-Object -First 3; Pop-Location
    $exe = Get-ChildItem "$maui\bin\Debug\net10.0-windows10.0.19041.0" -Recurse -Filter SistemaConversorUI.Maui.exe | Select-Object -First 1 -ExpandProperty FullName
    if ($exe) { Start-Process $exe; Ok 'Escritorio abierto' } else { Falla 'No se compilo el escritorio MAUI' }
    Paso 'Cliente movil (MAUI Android)'
    $u = Unidad-Corta $T
    Push-Location "$u\04. CLIMOV\SistemaConversorUI.Maui"
    & $dotnet build -f net10.0-android -nologo -v q "-p:AndroidSdkDirectory=$env:ANDROID_HOME" "-p:JavaSdkDirectory=$jbr" 2>&1 | Select-String ' error ' | Select-Object -First 3
    Pop-Location; subst Q: /D | Out-Null
    $apk = Get-ChildItem "$maui\bin\Debug\net10.0-android" -Recurse -Filter '*-Signed.apk' | Select-Object -First 1 -ExpandProperty FullName
    Celular $apk 'com.companyname.sistemaconversorui.maui' @(8080)
    $urls = @("Servidor (WSDL): http://${IP}:8080/WS_ConUni_Java_GR02/WSConversorUnidades?wsdl", "Web:             http://${IP}:5240")
}

# ================================================================== TI.1.2  SOAP .NET
if ($TI -eq '1.2') {
    $T = Carpeta 'TI.1.2*'
    $puertos = 8085, 5015
    $credenciales = 'MONSTER / MONSTER9'
    $url = "http://${IP}:8085/Service1.svc"
    Paso 'Configurando la IP en los clientes'
    Fijar (Buscar "$T\04. CLICON" 'App.config') '(key="UrlServicioSOAP"\s+value=")[^"]*(")' "`${1}$url`${2}" 'Consola: App.config'
    Fijar (Buscar "$T\04. CLIESC" 'App.config') '(key="UrlServicioSOAP"\s+value=")[^"]*(")' "`${1}$url`${2}" 'Escritorio: App.config'
    Fijar (Buscar "$T\04. CLIWEB" 'appsettings.json') '("Endpoint"\s*:\s*")[^"]*(")' "`${1}$url`${2}" 'Web: appsettings.json'
    Fijar (Buscar "$T\04. CLIMOV" 'ConfiguracionServidor.kt') '(SERVER_IP\s*=\s*")[^"]*(")' "`${1}$IPMovil`${2}" 'Movil: ConfiguracionServidor.kt'
    Paso 'Firewall'; Firewall $puertos
    Paso 'Servidor SOAP .NET (IIS Express)'
    Get-Process iisexpress -ErrorAction SilentlyContinue | Stop-Process -Force
    $srv = Split-Path (Buscar "$T\04. SERVIDOR" 'iniciar_servidor.ps1') -Parent
    Ventana "TI 1.2 - Servidor" $srv @('powershell -ExecutionPolicy Bypass -File iniciar_servidor.ps1') -Minimizada
    if (EsperarUrl 'http://localhost:8085/Service1.svc?wsdl' 120) { Ok 'Servidor respondiendo' } else { Falla 'El servidor no responde' }
    Paso 'Compilando consola y escritorio (.NET Framework)'
    foreach ($c in 'CLICON', 'CLIESC') {
        $sln = Get-ChildItem "$T\04. $c" -Recurse -Depth 1 -Filter *.sln | Select-Object -First 1 -ExpandProperty FullName
        & $msbuild $sln -t:Rebuild -p:Configuration=Debug -nologo -v:q -nr:false 2>&1 | Select-String ' error ' | Select-Object -First 3
    }
    Ok 'Compilados'
    Paso 'Cliente consola'
    Ventana "TI 1.2 - Consola" 'C:\ConUni\SOAP_DOTNET\CLICON\Debug' @('CONUNI_SOAP_DOTNET_CLICON_G02.exe')
    Ok 'Ventana de consola abierta'
    Paso 'Cliente escritorio'
    Get-Process CONUNI_SOAP_DOTNET_CLIESC_G02 -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Process 'C:\ConUni\SOAP_DOTNET\CLIESC\Debug\CONUNI_SOAP_DOTNET_CLIESC_G02.exe'; Ok 'Escritorio abierto'
    Paso 'Cliente web (ASP.NET Core)'
    Liberar-Puerto 5015
    $web = Split-Path (Buscar "$T\04. CLIWEB" 'CONUNI_SOAP_DOTNET_CLIWEB_G02.csproj') -Parent
    Push-Location $web; & $dotnet build -nologo -v q 2>&1 | Select-String ' error ' | Select-Object -First 3; Pop-Location
    Ventana "TI 1.2 - Web" $web @("`"$dotnet`" run --no-build --launch-profile http") -Minimizada
    if (EsperarPuerto 5015 60) { Ok 'Web lista'; Start-Process "http://${IP}:5015" } else { Falla 'La web no arranco' }
    Paso 'Cliente movil (Android)'
    $proy = Split-Path (Buscar "$T\04. CLIMOV" 'settings.gradle.kts') -Parent
    $apk = Gradle-Apk $proy
    Celular $apk 'ec.edu.monster.soapdotnet' @(8085)
    $urls = @("Servidor (WSDL): http://${IP}:8085/Service1.svc?wsdl", "Web:             http://${IP}:5015")
}

# ================================================================== TI.1.3  REST Java
if ($TI -eq '1.3') {
    $T = Carpeta 'TI.1.3*'
    $puertos = @(8080)
    $credenciales = 'MONSTER / MONSTER9'
    $api = "http://${IP}:8080/CONUNI_RESTFUL_JAVA_GR02/api"
    Paso 'Configurando la IP en los clientes'
    foreach ($c in 'CLICON', 'CLIESC', 'CLIWEB') {
        $f = Get-ChildItem "$T\04. $c" -Recurse -Filter config.properties | Where-Object { $_.FullName -match '\\src\\main\\resources\\' } | Select-Object -First 1 -ExpandProperty FullName
        Fijar $f '(api\.base\.url=)\S*' "`${1}$api" "$c`: config.properties"
    }
    Fijar (Buscar "$T\04. CLIMOV" 'RetrofitClient.kt') '(SERVER_IP\s*=\s*")[^"]*(")' "`${1}$IPMovil`${2}" 'Movil: RetrofitClient.kt'
    Paso 'Firewall'; Firewall $puertos
    Paso 'Servidor REST y cliente web en Payara'
    $script = Buscar "$T\04. SERVIDOR" 'iniciar_servidor.ps1'
    # en un proceso aparte: los avisos de Maven en stderr cortarian este script
    powershell -NoProfile -ExecutionPolicy Bypass -File "$script" 2>&1 | Select-String -Pattern 'deployed|failed|ERROR' | Select-Object -Last 4 | Out-Host
    if (EsperarUrl 'http://localhost:8080/CONUNI_RESTFUL_JAVA_GR02/api/conversion/info' 60) { Ok 'API respondiendo' } else { Falla 'La API no responde' }
    if (EsperarUrl 'http://localhost:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02/' 30) { Ok 'Web lista'; Start-Process "http://${IP}:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02/" } else { Falla 'La web no responde' }
    Paso 'Cliente consola (Java)'
    $con = Split-Path (Get-ChildItem "$T\04. CLICON" -Recurse -Depth 1 -Filter pom.xml | Select-Object -First 1 -ExpandProperty FullName) -Parent
    Push-Location $con; mvn -q clean package -DskipTests 2>&1 | Select-String 'ERROR' | Select-Object -First 3; Pop-Location
    $jar = Get-ChildItem "$con\target\*.jar" | Where-Object { $_.Name -notlike 'original-*' } | Select-Object -First 1 -ExpandProperty Name
    Ventana "TI 1.3 - Consola" $con @("java -jar target\$jar")
    Ok 'Ventana de consola abierta'
    Paso 'Cliente escritorio (Java Swing)'
    $esc = Split-Path (Get-ChildItem "$T\04. CLIESC" -Recurse -Depth 1 -Filter pom.xml | Select-Object -First 1 -ExpandProperty FullName) -Parent
    Ventana "TI 1.3 - Escritorio" $esc @('mvn -q clean compile exec:java') -Minimizada
    Ok 'Escritorio iniciando (tarda unos segundos)'
    Paso 'Cliente movil (Android)'
    $proy = Split-Path (Buscar "$T\04. CLIMOV" 'settings.gradle.kts') -Parent
    $apk = Gradle-Apk $proy
    Celular $apk 'ec.edu.monster.restfuljava' @(8080)
    $urls = @("API:  $api/conversion/info", "Web:  http://${IP}:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02/")
}

# ================================================================== TI.1.4  REST .NET
if ($TI -eq '1.4') {
    $T = Carpeta 'TI.1.4*'
    $puertos = 5014, 3000
    $credenciales = 'MONSTER / Monster9'
    $api = "http://${IP}:5014/api/conversor"
    Paso 'Configurando la IP en los clientes'
    Fijar (Buscar "$T\04. CLIMOV" 'ApiService.java') '(BASE_URL\s*=\s*")[^"]*(")' "`${1}http://${IPMovil}:5014/api/conversor`${2}" 'Movil: ApiService.java'
    Ok "Consola, escritorio y web reciben la URL al arrancar: $api"
    Paso 'Firewall'; Firewall $puertos
    Paso 'Servidor REST .NET'
    Liberar-Puerto 5014
    $srv = Split-Path (Buscar "$T\04. SERVIDOR" 'WS_ConUnid_DOTNET_RESTFUL_GR02.csproj') -Parent
    Ventana "TI 1.4 - Servidor" $srv @("`"$dotnet`" run --launch-profile http") -Minimizada
    if (EsperarPuerto 5014 120) { Ok 'Servidor escuchando en 5014' } else { Falla 'El servidor no arranco' }
    Paso 'Cliente web (Flask)'
    Liberar-Puerto 3000
    Ventana "TI 1.4 - Web" "$T\04. CLIWEB" @("set CONUNI_URL=$api", 'python app.py') -Minimizada
    if (EsperarPuerto 3000 60) { Ok 'Web lista'; Start-Process "http://${IP}:3000" } else { Falla 'La web no arranco' }
    foreach ($par in @(@('CLICON', 'Consola'), @('CLIESC', 'Escritorio'))) {
        Paso "Cliente $($par[1].ToLower()) (Java)"
        $dir = Split-Path (Get-ChildItem "$T\04. $($par[0])" -Recurse -Depth 1 -Filter pom.xml | Select-Object -First 1 -ExpandProperty FullName) -Parent
        Push-Location $dir; mvn -q clean package -DskipTests 2>&1 | Select-String 'ERROR' | Select-Object -First 3; Pop-Location
        $jar = Get-ChildItem "$dir\target\*-jar-with-dependencies.jar" | Select-Object -First 1 -ExpandProperty Name
        if ($par[0] -eq 'CLICON') {
            Ventana "TI 1.4 - Consola" $dir @("java -Dconuni.url=$api -jar target\$jar")
        } else {
            Ventana "TI 1.4 - Escritorio" $dir @("java -Dconuni.url=$api -jar target\$jar") -Minimizada
        }
        Ok "$($par[1]) abierto"
    }
    Paso 'Cliente movil (Android)'
    $proy = Split-Path (Buscar "$T\04. CLIMOV" 'settings.gradle.kts') -Parent
    $apk = Gradle-Apk $proy -Wrapper
    Celular $apk 'ec.edu.monster' @(5014)
    $urls = @("API:  $api", "Web:  http://${IP}:3000")
}

# ------------------------------------------------------------------ resumen
Write-Host ''
Write-Host '=================================================================' -ForegroundColor Magenta
Write-Host "  TI.$TI LISTO   IP: $IP   Usuario / clave: $credenciales" -ForegroundColor Magenta
foreach ($u in $urls) { Write-Host "  $u" -ForegroundColor Magenta }
Write-Host '  Desde el celular (navegador) tambien se abre la web con la IP de arriba.' -ForegroundColor Magenta
Write-Host '=================================================================' -ForegroundColor Magenta
if ($script:fallas.Count) {
    Write-Host 'Revisar:' -ForegroundColor Red
    $script:fallas | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
}
