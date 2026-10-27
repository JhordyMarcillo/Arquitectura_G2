# Script para probar el servidor REST (contrato con token)
# Uso: .\probar-servidor.ps1            -> usa 192.168.137.1
#      .\probar-servidor.ps1 localhost

param([string]$ip = "192.168.137.1")

$url = "http://${ip}:5014/api/conversor"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Probador de Servidor REST - MONSTER  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "URL: $url" -ForegroundColor Gray
Write-Host ""

function Llamar($metodo, $ruta, $cuerpo, $token) {
    $headers = @{}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    $parametros = @{ Uri = "$url$ruta"; Method = $metodo; Headers = $headers; TimeoutSec = 5; ContentType = "application/json" }
    if ($cuerpo) { $parametros["Body"] = ($cuerpo | ConvertTo-Json) }
    try {
        return Invoke-RestMethod @parametros
    } catch {
        # 400/401/500: el servidor envia el JSON de error en el cuerpo
        if ($_.ErrorDetails.Message) { return $_.ErrorDetails.Message | ConvertFrom-Json }
        if ($_.Exception.Response) {
            $lector = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            return $lector.ReadToEnd() | ConvertFrom-Json
        }
        Write-Host "   ❌ No se pudo conectar: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host '   Inicia el servidor: dotnet run --launch-profile http (escucha en 0.0.0.0:5014)' -ForegroundColor Yellow
        exit
    }
}

Write-Host "1. Login (CU-01)..." -ForegroundColor Yellow
$login = Llamar "POST" "/login" @{ usuario = "MONSTER"; contrasenia = "Monster9" } $null
Write-Host "   $($login.codigo) $($login.mensaje)" -ForegroundColor Green
$token = $login.token

Write-Host "2. Categorias (CU-02)..." -ForegroundColor Yellow
$cat = Llamar "GET" "/categorias" $null $token
Write-Host "   $($cat.categorias -join ', ')" -ForegroundColor Green

Write-Host "3. Unidades de MASA (CU-03)..." -ForegroundColor Yellow
$uni = Llamar "GET" "/categorias/MASA/unidades" $null $token
Write-Host "   $($uni.unidades -join ', ')" -ForegroundColor Green

Write-Host "4. 25 CELSIUS -> FAHRENHEIT (CU-04)..." -ForegroundColor Yellow
$conv = Llamar "POST" "/convertir" @{ categoria = "TEMPERATURA"; unidadOrigen = "CELSIUS"; unidadDestino = "FAHRENHEIT"; valor = 25 } $token
Write-Host "   Resultado: $($conv.resultado) ($($conv.mensaje))" -ForegroundColor Green

Write-Host "5. Sin token (CU-08)..." -ForegroundColor Yellow
$sin = Llamar "GET" "/categorias" $null $null
Write-Host "   $($sin.codigo) $($sin.mensaje)" -ForegroundColor Magenta

Write-Host "6. Unidad TONELADA (CU-10)..." -ForegroundColor Yellow
$ton = Llamar "POST" "/convertir" @{ categoria = "MASA"; unidadOrigen = "TONELADA"; unidadDestino = "KILOGRAMO"; valor = 1 } $token
Write-Host "   $($ton.codigo) $($ton.mensaje)" -ForegroundColor Magenta

Write-Host ""
Write-Host "En ApiService.java usa:" -ForegroundColor Cyan
Write-Host "   private static final String BASE_URL = `"$url`";" -ForegroundColor White
