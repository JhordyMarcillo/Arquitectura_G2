"""
Prueba de contrato de los servidores REST EurekaBank (TI.1.3 Java y TI.1.4 .NET).

Verifica, contra un servidor en ejecucion, que cumpla CONTRATO_EUREKABANK_GR02.md:
rutas, formato JSON, reglas de negocio (saldos, costos, movimientos) y codigos de error.
Solo usa la biblioteca estandar de Python.

Uso:
    python test_contrato_rest.py http://localhost:5080/api/eurekabank
    python test_contrato_rest.py http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank

Nota: registra movimientos reales (depositos, retiros, transferencias) en la base.
"""
import json
import sys
import urllib.error
import urllib.request
from decimal import Decimal

BASE = sys.argv[1].rstrip("/") if len(sys.argv) > 1 else "http://localhost:5080/api/eurekabank"
TOKEN = None
fallos = 0
total = 0


def llamar(metodo, ruta, cuerpo=None, token=True):
    datos = json.dumps(cuerpo).encode() if cuerpo is not None else None
    peticion = urllib.request.Request(BASE + ruta, data=datos, method=metodo)
    peticion.add_header("Content-Type", "application/json")
    if token and TOKEN:
        peticion.add_header("Authorization", "Bearer " + TOKEN)
    try:
        with urllib.request.urlopen(peticion, timeout=15) as r:
            return r.status, json.loads(r.read().decode(), parse_float=Decimal)
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode() or "{}", parse_float=Decimal)


def verificar(nombre, condicion, detalle=""):
    global fallos, total
    total += 1
    if condicion:
        print(f"  OK    {nombre}")
    else:
        fallos += 1
        print(f"  FALLA {nombre} {detalle}")


def error_esperado(nombre, respuesta, http, codigo):
    estado, cuerpo = respuesta
    verificar(nombre, estado == http and cuerpo.get("estado") == "ERROR" and cuerpo.get("codigo") == codigo,
              f"-> HTTP {estado} {cuerpo}")


def saldo(cuenta):
    _, cuerpo = llamar("GET", f"/cuentas/{cuenta}/movimientos")
    return Decimal(str(cuerpo["saldo"])), len(cuerpo["movimientos"])


print(f"Servidor: {BASE}\n")

print("1. Autenticacion")
estado, cuerpo = llamar("POST", "/login", {"usuario": "cromero", "clave": "chicho"}, token=False)
verificar("login correcto devuelve token", estado == 200 and cuerpo.get("estado") == "OK" and cuerpo.get("token"), cuerpo)
verificar("login devuelve empleado 0001", cuerpo.get("codigoEmpleado") == "0001" and cuerpo.get("nombreEmpleado"), cuerpo)
verificar("expiracion con formato yyyy-MM-ddTHH:mm:ss", len(cuerpo.get("expiracion", "")) == 19, cuerpo.get("expiracion"))
TOKEN = cuerpo.get("token")
error_esperado("clave incorrecta -> AUTH-003", llamar("POST", "/login", {"usuario": "cromero", "clave": "x"}, False), 401, "AUTH-003")
error_esperado("usuario ANULADO -> AUTH-003", llamar("POST", "/login", {"usuario": "creyes", "clave": "linda"}, False), 401, "AUTH-003")
error_esperado("datos vacios -> DATA-001", llamar("POST", "/login", {"usuario": "", "clave": ""}, False), 400, "DATA-001")
error_esperado("sin token -> AUTH-001", llamar("GET", "/cuentas", token=False), 401, "AUTH-001")
TOKEN_REAL, TOKEN = TOKEN, "token-falso"
error_esperado("token falso -> AUTH-001", llamar("GET", "/cuentas"), 401, "AUTH-001")
TOKEN = TOKEN_REAL

print("\n2. Consultas")
estado, cuerpo = llamar("GET", "/cuentas")
cuentas = {c["codigo"]: c for c in cuerpo.get("cuentas", [])}
verificar("lista las 6 cuentas", estado == 200 and len(cuentas) == 6, len(cuentas))
verificar("campos de cuenta", set(cuentas.get("00100001", {})) >= {"codigo", "cliente", "moneda", "saldo", "estado"}, cuentas.get("00100001"))
estado, cuerpo = llamar("GET", "/cuentas/00200001/movimientos")
mov = cuerpo.get("movimientos", [{}])[0]
verificar("movimientos de 00200001", estado == 200 and cuerpo.get("moneda") == "Soles" and len(cuerpo["movimientos"]) >= 15, cuerpo.get("mensaje"))
verificar("campos de movimiento", set(mov) >= {"numero", "fecha", "tipo", "descripcion", "accion", "importe", "cuentaReferencia"}, mov)
verificar("fecha yyyy-MM-dd", mov.get("fecha") == "2008-01-05", mov.get("fecha"))
error_esperado("cuenta inexistente -> CTA-001", llamar("GET", "/cuentas/99999999/movimientos"), 404, "CTA-001")

print("\n3. Deposito (saldo += importe - costo 2.00, dos movimientos)")
antes, n = saldo("00100001")
estado, cuerpo = llamar("POST", "/depositos", {"cuenta": "00100001", "importe": 100})
verificar("deposito OK", estado == 200 and cuerpo.get("estado") == "OK", cuerpo)
verificar("saldo devuelto = antes + 98.00", Decimal(str(cuerpo.get("saldo"))) == antes + Decimal("98.00"), f"{antes} -> {cuerpo.get('saldo')}")
despues, n2 = saldo("00100001")
verificar("se registraron 2 movimientos", n2 == n + 2, f"{n} -> {n2}")
_, cuerpo = llamar("GET", "/cuentas/00100001/movimientos")
ult = cuerpo["movimientos"][-2:]
verificar("movimientos 003 y 010", [m["tipo"] for m in ult] == ["003", "010"], ult)
error_esperado("importe 0 -> DATA-001", llamar("POST", "/depositos", {"cuenta": "00100001", "importe": 0}), 400, "DATA-001")
error_esperado("cuenta CANCELADA -> CTA-002", llamar("POST", "/depositos", {"cuenta": "00300001", "importe": 10}), 409, "CTA-002")
error_esperado("cuenta inexistente -> CTA-001", llamar("POST", "/depositos", {"cuenta": "99999999", "importe": 10}), 404, "CTA-001")

print("\n4. Retiro (saldo -= importe + costo)")
antes, _ = saldo("00100001")
estado, cuerpo = llamar("POST", "/retiros", {"cuenta": "00100001", "clave": "123456", "importe": 50})
verificar("retiro OK y saldo = antes - 52.00", estado == 200 and Decimal(str(cuerpo.get("saldo"))) == antes - Decimal("52.00"), cuerpo)
error_esperado("clave incorrecta -> CTA-003", llamar("POST", "/retiros", {"cuenta": "00100001", "clave": "000000", "importe": 1}), 401, "CTA-003")
error_esperado("saldo insuficiente -> CTA-004", llamar("POST", "/retiros", {"cuenta": "00100001", "clave": "123456", "importe": 9999999}), 409, "CTA-004")
despues, _ = saldo("00100001")
verificar("un error no modifica el saldo", despues == antes - Decimal("52.00"), f"{antes} -> {despues}")

print("\n5. Transferencia")
o1, _ = saldo("00100001")
d1, _ = saldo("00200001")
estado, cuerpo = llamar("POST", "/transferencias", {"cuentaOrigen": "00100001", "claveOrigen": "123456", "cuentaDestino": "00200001", "importe": 25})
verificar("transferencia OK", estado == 200 and cuerpo.get("estado") == "OK" and cuerpo.get("cuenta") == "00100001", cuerpo)
o2, _ = saldo("00100001")
d2, _ = saldo("00200001")
verificar("origen -= 27.00", o2 == o1 - Decimal("27.00"), f"{o1} -> {o2}")
verificar("destino += 23.00", d2 == d1 + Decimal("23.00"), f"{d1} -> {d2}")
_, cuerpo = llamar("GET", "/cuentas/00200001/movimientos")
ingreso = cuerpo["movimientos"][-2]
verificar("destino registra 008 con referencia al origen", ingreso["tipo"] == "008" and ingreso["cuentaReferencia"] == "00100001", ingreso)
error_esperado("monedas distintas -> CTA-005", llamar("POST", "/transferencias", {"cuentaOrigen": "00100001", "claveOrigen": "123456", "cuentaDestino": "00100002", "importe": 1}), 409, "CTA-005")
error_esperado("misma cuenta -> CTA-006", llamar("POST", "/transferencias", {"cuentaOrigen": "00100001", "claveOrigen": "123456", "cuentaDestino": "00100001", "importe": 1}), 400, "CTA-006")
error_esperado("destino cancelado -> CTA-002", llamar("POST", "/transferencias", {"cuentaOrigen": "00100001", "claveOrigen": "123456", "cuentaDestino": "00300001", "importe": 1}), 409, "CTA-002")

print(f"\nResultado: {total - fallos}/{total} verificaciones correctas")
sys.exit(1 if fallos else 0)
