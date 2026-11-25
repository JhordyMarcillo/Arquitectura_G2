<#
  Crea y carga la base EUREKABANK en la instancia de Amazon RDS for SQL Server.

  Uso:
      powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1            # solo si la base no existe
      powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1 -Recrear   # borra y recrea la base

  Lee el host y la clave del login de la aplicacion del archivo .env de esta carpeta.
  El administrador de la instancia (master user) se toma de:
      - $env:DB_ADMIN_USER / $env:DB_ADMIN_PASSWORD, o
      - %USERPROFILE%\.aws\rds-arqg2-credenciales.txt (lineas USUARIO= y PASSWORD=).
  Requiere sqlcmd (viene con las herramientas de SQL Server / ODBC Driver).
#>
param([switch]$Recrear)

$ErrorActionPreference = "Stop"
$aqui = Split-Path -Parent $MyInvocation.MyCommand.Path

function Leer-Env($ruta) {
    $h = @{}
    foreach ($l in Get-Content -LiteralPath $ruta -Encoding UTF8) {
        $l = $l.Trim()
        if ($l -eq "" -or $l.StartsWith("#")) { continue }
        $i = $l.IndexOf("=")
        if ($i -gt 0) { $h[$l.Substring(0, $i).Trim()] = $l.Substring($i + 1).Trim() }
    }
    return $h
}

$rutaEnv = Join-Path $aqui ".env"
if (-not (Test-Path -LiteralPath $rutaEnv)) {
    throw "No existe .env. Copia .env.example como .env y completa los valores."
}
$cfg = Leer-Env $rutaEnv
foreach ($k in "DB_HOST", "DB_PORT", "EUREKA_PASSWORD") {
    if (-not $cfg[$k] -or $cfg[$k].StartsWith("<")) { throw "Falta $k en .env" }
}

# --- sqlcmd ---
$sqlcmd = (Get-Command sqlcmd -ErrorAction SilentlyContinue).Source
if (-not $sqlcmd) {
    $sqlcmd = Get-ChildItem "C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\*\Tools\Binn\SQLCMD.EXE" -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending | Select-Object -First 1 -ExpandProperty FullName
}
if (-not $sqlcmd) { throw "No se encontro sqlcmd. Instala las herramientas de linea de comandos de SQL Server." }

# --- administrador de la instancia ---
$adminUser = if ($env:DB_ADMIN_USER) { $env:DB_ADMIN_USER } elseif ($cfg["DB_ADMIN_USER"]) { $cfg["DB_ADMIN_USER"] } else { "admin" }
$adminPass = $env:DB_ADMIN_PASSWORD
if (-not $adminPass) {
    $cred = Join-Path $env:USERPROFILE ".aws\rds-arqg2-credenciales.txt"
    if (Test-Path $cred) {
        $adminPass = (Get-Content $cred | Where-Object { $_ -like "PASSWORD=*" }) -replace "^PASSWORD=", ""
    }
}
if (-not $adminPass) { throw "Define DB_ADMIN_PASSWORD o crea %USERPROFILE%\.aws\rds-arqg2-credenciales.txt" }

$servidor = "$($cfg['DB_HOST']),$($cfg['DB_PORT'])"
$env:SQLCMDPASSWORD = $adminPass   # evita escribir la clave en la linea de comandos
$comun = @("-S", $servidor, "-U", $adminUser, "-C", "-b", "-f", "65001")

Write-Host "Servidor: $servidor (usuario $adminUser)"
$existe = & $sqlcmd @comun -h -1 -W -Q "SET NOCOUNT ON; SELECT CASE WHEN DB_ID('EUREKABANK') IS NULL THEN 0 ELSE 1 END"
if ($LASTEXITCODE -ne 0) { throw "No se pudo conectar a $servidor. Revisa que la RDS este disponible y que tu IP este en el security group (puerto 1433)." }
$existe = ($existe | Select-Object -First 1).Trim()

if ($existe -eq "1" -and -not $Recrear) {
    Write-Host "EUREKABANK ya existe: no se ejecutan los scripts (usa -Recrear para empezar de cero)."
    return
}

Write-Host "Creando EUREKABANK (17 tablas y login de la aplicacion)..."
& $sqlcmd @comun -v "EUREKA_PASSWORD=$($cfg['EUREKA_PASSWORD'])" -i (Join-Path $aqui "Crea_BD.sql")
if ($LASTEXITCODE -ne 0) { throw "Fallo Crea_BD.sql" }

Write-Host "Cargando los datos de prueba..."
& $sqlcmd @comun -i (Join-Path $aqui "Carga_Datos.sql")
if ($LASTEXITCODE -ne 0) { throw "Fallo Carga_Datos.sql" }

$tablas = & $sqlcmd @comun -d EUREKABANK -h -1 -W -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.tables"
$filas = & $sqlcmd @comun -d EUREKABANK -h -1 -W -Q "SET NOCOUNT ON; SELECT (SELECT COUNT(*) FROM Usuario) AS usuarios"
Write-Host "EUREKABANK creada y cargada en Amazon RDS. Tablas: $(($tablas | Select-Object -First 1).Trim())  Usuarios: $(($filas | Select-Object -First 1).Trim())"
Remove-Item Env:SQLCMDPASSWORD -ErrorAction SilentlyContinue
