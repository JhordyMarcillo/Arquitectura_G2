# Compila y levanta el servidor SOAP (WCF) en IIS Express, puerto 8085.
# - Como Administrador: queda accesible desde otros equipos/celulares de la red
#   (crea la reserva de URL y la regla de firewall, una sola vez).
# - Sin Administrador: queda accesible solo en http://localhost:8085/Service1.svc
param([int]$Puerto = 8085)

$ErrorActionPreference = 'Stop'
$proyecto = Join-Path $PSScriptRoot 'CONUNI_SOAP_DOTNET_GR02'
$msbuild  = 'C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe'
$iis      = 'C:\Program Files\IIS Express\iisexpress.exe'
$esAdmin  = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

& $msbuild (Join-Path $proyecto 'CONUNI_SOAP_DOTNET_GR02.csproj') -t:Build -p:Configuration=Debug -v:minimal -nologo
if ($LASTEXITCODE -ne 0) { throw 'Fallo la compilacion del servidor' }

if ($esAdmin) {
    if (-not (netsh http show urlacl url="http://*:$Puerto/" | Select-String "http://\*:$Puerto/")) {
        netsh http add urlacl url="http://*:$Puerto/" user=Everyone | Out-Null
    }
    if (-not (Get-NetFirewallRule -DisplayName "SOAP DOTNET $Puerto" -ErrorAction SilentlyContinue)) {
        New-NetFirewallRule -DisplayName "SOAP DOTNET $Puerto" -Direction Inbound -Protocol TCP -LocalPort $Puerto -Action Allow -Profile Any | Out-Null
    }
    $binding = "*:${Puerto}:"
} else {
    Write-Warning 'Sin permisos de Administrador: el servidor solo respondera en localhost. Ejecuta este script como Administrador para abrirlo a la red.'
    $binding = "*:${Puerto}:localhost"
}

# applicationhost.config propio (a partir de la plantilla de IIS Express)
$dirCfg = Join-Path $env:TEMP 'conuni_soap_iis'
New-Item -ItemType Directory -Force $dirCfg | Out-Null
$cfg = Join-Path $dirCfg 'applicationhost.config'
[xml]$xml = Get-Content (Join-Path $env:ProgramFiles 'IIS Express\config\templates\PersonalWebServer\applicationhost.config')
$sites = $xml.configuration.'system.applicationHost'.sites
$sites.site | ForEach-Object { [void]$sites.RemoveChild($_) }
$site = $xml.CreateElement('site'); $site.SetAttribute('name', 'CONUNI_SOAP'); $site.SetAttribute('id', '1')
$app = $xml.CreateElement('application'); $app.SetAttribute('path', '/')
$vd = $xml.CreateElement('virtualDirectory'); $vd.SetAttribute('path', '/'); $vd.SetAttribute('physicalPath', $proyecto)
[void]$app.AppendChild($vd); [void]$site.AppendChild($app)
$b = $xml.CreateElement('bindings'); $bn = $xml.CreateElement('binding')
$bn.SetAttribute('protocol', 'http'); $bn.SetAttribute('bindingInformation', $binding)
[void]$b.AppendChild($bn); [void]$site.AppendChild($b); [void]$sites.AppendChild($site)
$xml.Save($cfg)

Write-Host "Servidor SOAP: http://localhost:$Puerto/Service1.svc  (WSDL: ?wsdl)" -ForegroundColor Green
Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.*' -and $_.InterfaceAlias -notlike '*VMware*' -and $_.InterfaceAlias -notlike '*vEthernet*' } |
    ForEach-Object { Write-Host "  Desde otra red/equipo: http://$($_.IPAddress):$Puerto/Service1.svc" }
& $iis /config:$cfg /site:CONUNI_SOAP
