# Carga la base eurekabank en Amazon RDS y deja la conexion en ".env" (no se sube a git).
# Uso:  powershell -ExecutionPolicy Bypass -File .\cargar_bdd_nube.ps1 [-Recrear]
# -Recrear borra y vuelve a cargar SOLO la base eurekabank.
param(
    [string]$Region = "us-east-2",
    [string]$Instancia = "gpcaceres-mysql",
    [string]$Credenciales = "$env:USERPROFILE\.aws\rds-gpcaceres-credenciales.txt",
    [string]$UsuarioApp = "eureka_nube",
    [switch]$Recrear
)
# El servidor se toma de .env si ya existe; si no, se consulta en AWS
$archivoEnv = Join-Path $PSScriptRoot ".env"
$servidor = $null
if ((Test-Path -LiteralPath $archivoEnv) -and ((Get-Content -LiteralPath $archivoEnv -Raw) -match 'jdbc:mysql://([^:/]+)')) { $servidor = $matches[1] }
if (-not $servidor) {
    $aws = "C:\Program Files\Amazon\AWSCLIV2\aws.exe"
    if (-not (Test-Path $aws)) { $aws = "aws" }
    $servidor = & $aws rds describe-db-instances --region $Region --db-instance-identifier $Instancia --query "DBInstances[0].Endpoint.Address" --output text
    if ($LASTEXITCODE -ne 0) { Write-Host "No se encontro la instancia. Inicie sesion con: aws login --region $Region"; exit 1 }
}
$lineas = Get-Content $Credenciales
$admin = ($lineas | Where-Object { $_ -match '^\W*USUARIO=' }) -replace '^\W*USUARIO=', ''
$env:RDS_ADMIN_PASSWORD = ($lineas | Where-Object { $_ -like 'PASSWORD=*' }) -replace '^PASSWORD=', ''

if (Test-Path -LiteralPath $archivoEnv) {
    $env:DB_PASSWORD = (Get-Content -LiteralPath $archivoEnv | Where-Object { $_ -like 'DB_PASSWORD=*' }) -replace '^DB_PASSWORD=', ''
} else {
    $letras = [char[]]"ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789"
    $bytes = New-Object byte[] 24
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    $env:DB_PASSWORD = -join ($bytes | ForEach-Object { $letras[$_ % $letras.Length] })
    $contenido = "DB_URL=jdbc:mysql://${servidor}:3306/eurekabank?sslMode=REQUIRED&fallbackToSystemKeyStore=false&serverTimezone=America/Guayaquil`nDB_USER=$UsuarioApp`nDB_PASSWORD=$env:DB_PASSWORD`n"
    [IO.File]::WriteAllText($archivoEnv, $contenido, (New-Object Text.UTF8Encoding($false)))
}

$driver = Get-ChildItem "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j" -Recurse -Filter "mysql-connector-j-*.jar" -ErrorAction SilentlyContinue |
    Sort-Object Name -Descending | Select-Object -First 1
if (-not $driver) { Write-Host "Falta el driver de MySQL: compile primero el servidor con 'mvn package'."; exit 1 }
$argumentos = @('-cp', $driver.FullName, (Join-Path $PSScriptRoot 'CargarBdd.java'), $servidor, $admin, $UsuarioApp, $PSScriptRoot)
if ($Recrear) { $argumentos += 'recrear' }
& java $argumentos
$codigo = $LASTEXITCODE
Remove-Item Env:RDS_ADMIN_PASSWORD, Env:DB_PASSWORD
exit $codigo
