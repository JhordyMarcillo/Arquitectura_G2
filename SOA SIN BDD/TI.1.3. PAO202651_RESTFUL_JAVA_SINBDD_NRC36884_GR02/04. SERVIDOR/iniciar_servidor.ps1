# Compila y despliega en Payara (puerto 8080):
#   - el servidor RESTful   -> http://<IP>:8080/CONUNI_RESTFUL_JAVA_GR02/api
#   - el cliente web (JSP)  -> http://<IP>:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02
# Como Administrador además abre el puerto 8080 en el firewall (para otros equipos/celulares).
param([string]$Payara = "$env:USERPROFILE\Documents\payara7")

$ErrorActionPreference = 'Stop'
$asadmin  = Join-Path $Payara 'bin\asadmin.bat'
$servidor = Join-Path $PSScriptRoot 'CONUNI_RESTFUL_JAVA_GR02'
$cliweb   = Join-Path $PSScriptRoot '..\04. CLIWEB\CONUNI_RESTFUL_JAVA_CLIWEB_G02'
if (-not (Test-Path $asadmin)) { throw "No se encontro Payara en $Payara (usa -Payara <ruta>)" }

function Desplegar($proyecto, $contexto) {
    Push-Location $proyecto
    try {
        mvn -q -DskipTests clean package
        if ($LASTEXITCODE -ne 0) { throw "Fallo la compilacion de $proyecto" }
        $war = Get-ChildItem target\*.war | Select-Object -First 1
        & $asadmin deploy --force=true --name $contexto --contextroot $contexto $war.FullName
    } finally { Pop-Location }
}

if (-not (Get-NetTCPConnection -LocalPort 4848 -State Listen -ErrorAction SilentlyContinue)) {
    & $asadmin start-domain domain1
}
Desplegar $servidor 'CONUNI_RESTFUL_JAVA_GR02'
Desplegar $cliweb   'CONUNI_RESTFUL_JAVA_CLIWEB_G02'

$esAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole('Administrator')
if ($esAdmin) {
    if (-not (Get-NetFirewallRule -DisplayName 'Payara 8080' -ErrorAction SilentlyContinue)) {
        New-NetFirewallRule -DisplayName 'Payara 8080' -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Any | Out-Null
    }
} else {
    Write-Warning 'Sin Administrador no se abre el firewall: otros equipos/celulares podrian no conectarse.'
}

Write-Host 'API REST:    http://localhost:8080/CONUNI_RESTFUL_JAVA_GR02/api/conversion/info' -ForegroundColor Green
Write-Host 'Cliente web: http://localhost:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02' -ForegroundColor Green
Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.*' -and $_.InterfaceAlias -notlike '*VMware*' -and $_.InterfaceAlias -notlike '*vEthernet*' } |
    ForEach-Object { Write-Host "  Desde otra red/equipo: http://$($_.IPAddress):8080/CONUNI_RESTFUL_JAVA_GR02/api" }
