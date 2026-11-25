# Crea una instancia RDS MySQL y una RDS SQL Server Express accesibles solo desde tu IP.
# Uso:  powershell -ExecutionPolicy Bypass -File .\crear_bases_rds.ps1 [-Region us-east-1]
param(
    [string]$Region = "",
    [string]$Prefijo = "arqg2",
    [string]$Usuario = "admin"
)
# "Continue": en PS 5.1 el stderr de aws.exe con "Stop" se vuelve error fatal; validamos con $LASTEXITCODE
$ErrorActionPreference = "Continue"
$aws = "C:\Program Files\Amazon\AWSCLIV2\aws.exe"
if (-not (Test-Path $aws)) { $aws = "aws" }

if (-not $Region) { $Region = (& $aws configure get region); if (-not $Region) { $Region = "us-east-1" } }
$r = @("--region", $Region)

Write-Host "Verificando credenciales..."
$cuenta = & $aws sts get-caller-identity --query Account --output text
if ($LASTEXITCODE -ne 0) { throw "No hay credenciales. Ejecuta 'aws configure' primero." }
Write-Host "Cuenta $cuenta, region $Region"

# Credenciales de la BD: se guardan fuera del repositorio
$credFile = Join-Path $env:USERPROFILE ".aws\rds-$Prefijo-credenciales.txt"
if (Test-Path $credFile) {
    $Password = (Get-Content $credFile | Where-Object { $_ -like "PASSWORD=*" }) -replace "^PASSWORD=", ""
} else {
    $chars = [char[]]"ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789"
    $Password = -join (1..20 | ForEach-Object { $chars | Get-Random })
    New-Item -ItemType Directory -Force (Split-Path $credFile) | Out-Null
    "USUARIO=$Usuario`nPASSWORD=$Password" | Out-File -Encoding utf8 $credFile
}

# Red: VPC por defecto + security group limitado a tu IP
$miIp = (Invoke-RestMethod "https://checkip.amazonaws.com").Trim()
$vpc = & $aws ec2 describe-vpcs @r --filters Name=isDefault,Values=true --query "Vpcs[0].VpcId" --output text
if (-not $vpc -or $vpc -eq "None") { throw "No hay VPC por defecto en $Region." }

$sgNombre = "$Prefijo-rds-sg"
$sg = & $aws ec2 describe-security-groups @r --filters Name=group-name,Values=$sgNombre Name=vpc-id,Values=$vpc --query "SecurityGroups[0].GroupId" --output text
if (-not $sg -or $sg -eq "None") {
    $sg = & $aws ec2 create-security-group @r --group-name $sgNombre --description "RDS MySQL/SQLServer Arquitectura G2" --vpc-id $vpc --query GroupId --output text
}
foreach ($puerto in 3306, 1433) {
    & $aws ec2 authorize-security-group-ingress @r --group-id $sg --protocol tcp --port $puerto --cidr "$miIp/32" 2>$null | Out-Null
}
Write-Host "Security group $sg abierto para $miIp en 3306 y 1433"

function Get-Clase($engine) {
    foreach ($c in "db.t3.micro", "db.t4g.micro", "db.t3.small") {
        # La CLI pagina: se devuelve una linea por pagina, por eso se une y se busca la clase
        $ok = & $aws rds describe-orderable-db-instance-options @r --engine $engine --db-instance-class $c --query "OrderableDBInstanceOptions[].DBInstanceClass" --output text
        if ((($ok -join " ") -split "\s+") -contains $c) { return $c }
    }
    throw "No se encontro clase disponible para $engine"
}

function New-Instancia($id, $engine, $extra) {
    $existe = & $aws rds describe-db-instances @r --db-instance-identifier $id --query "DBInstances[0].DBInstanceStatus" --output text 2>&1 | Where-Object { $_ -is [string] }
    if ($LASTEXITCODE -eq 0) { Write-Host "$id ya existe ($existe)"; return }
    $clase = Get-Clase $engine
    Write-Host "Creando $id ($engine, $clase)..."
    & $aws rds create-db-instance @r --db-instance-identifier $id --engine $engine --db-instance-class $clase `
        --allocated-storage 20 --storage-type gp2 --master-username $Usuario --master-user-password $Password `
        --vpc-security-group-ids $sg --publicly-accessible --backup-retention-period 0 --no-multi-az @extra `
        --query "DBInstance.DBInstanceIdentifier" --output text
    if ($LASTEXITCODE -ne 0) { throw "Fallo la creacion de $id" }
}

$idMy = "$Prefijo-mysql"
$idSql = "$Prefijo-sqlserver"
New-Instancia $idMy "mysql" @()
New-Instancia $idSql "sqlserver-ex" @("--license-model", "license-included")

Write-Host "Esperando a que las instancias esten disponibles (10-20 min)..."
& $aws rds wait db-instance-available @r --db-instance-identifier $idMy
& $aws rds wait db-instance-available @r --db-instance-identifier $idSql

foreach ($id in $idMy, $idSql) {
    $ep = & $aws rds describe-db-instances @r --db-instance-identifier $id --query "DBInstances[0].[Endpoint.Address,Endpoint.Port]" --output text
    Write-Host "$id -> $ep"
}
Write-Host "Usuario: $Usuario   (password en $credFile)"
