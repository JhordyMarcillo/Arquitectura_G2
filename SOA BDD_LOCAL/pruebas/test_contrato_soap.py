"""
Prueba de contrato de los servidores SOAP EurekaBank (TI.1.1 Java y TI.1.2 .NET).

Envia sobres SOAP 1.1 (document/literal wrapped, espacio de nombres http://ws.monster.edu.ec/)
y verifica reglas de negocio y codigos de error de CONTRATO_EUREKABANK_GR02.md.
Solo usa la biblioteca estandar de Python.

Uso:
    python test_contrato_soap.py http://localhost:5090/WSEurekaBank.svc
    python test_contrato_soap.py http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank

Nota: registra movimientos reales en la base.
"""
import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from decimal import Decimal
from xml.sax.saxutils import escape

URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:5090/WSEurekaBank.svc"
NS = "http://ws.monster.edu.ec/"
fallos = 0
total = 0


def operacion(nombre, **parametros):
    cuerpo = "".join(f"<{k}>{escape(str(v))}</{k}>" for k, v in parametros.items())
    sobre = ('<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body>'
             f'<{nombre} xmlns="{NS}">{cuerpo}</{nombre}></s:Body></s:Envelope>')
    peticion = urllib.request.Request(URL, data=sobre.encode("utf-8"), method="POST")
    peticion.add_header("Content-Type", "text/xml; charset=utf-8")
    peticion.add_header("SOAPAction", f'"{NS}WSEurekaBank/{nombre}"')
    try:
        with urllib.request.urlopen(peticion, timeout=15) as r:
            xml = r.read()
    except urllib.error.HTTPError as e:
        xml = e.read()
    raiz = ET.fromstring(xml)
    resultado = next((n for n in raiz.iter() if n.tag.split("}")[-1] == f"{nombre}Result" or n.tag.split("}")[-1] == "return"), None)
    if resultado is None:
        raise AssertionError(f"Respuesta SOAP inesperada: {xml[:300]!r}")
    return resultado


def texto(nodo, campo):
    for hijo in nodo:
        if hijo.tag.split("}")[-1] == campo:
            return hijo.text
    return None


def lista_repetida(nodo, campo):
    """Admite listas envueltas (WCF: <cuentas><DatosCuenta/>...) o repetidas (JAX-WS: <cuentas/><cuentas/>)."""
    hijos = [h for h in nodo if h.tag.split("}")[-1] == campo]
    if len(hijos) == 1 and len(hijos[0]) and all(len(n) for n in hijos[0]):
        return list(hijos[0])
    return hijos


def verificar(nombre, condicion, detalle=""):
    global fallos, total
    total += 1
    print(f"  {'OK   ' if condicion else 'FALLA'} {nombre} {'' if condicion else detalle}")
    if not condicion:
        fallos += 1


def error_esperado(nombre, nodo, codigo):
    verificar(nombre, texto(nodo, "estado") == "ERROR" and texto(nodo, "codigo") == codigo,
              f"-> {texto(nodo, 'estado')} {texto(nodo, 'codigo')} {texto(nodo, 'mensaje')}")


def saldo(token, cuenta):
    r = operacion("consultarMovimientos", token=token, cuenta=cuenta)
    return Decimal(texto(r, "saldo")), len(lista_repetida(r, "movimientos"))


print(f"Servicio: {URL}\n")

print("1. Autenticacion")
r = operacion("iniciarSesion", usuario="cromero", clave="chicho")
TOKEN = texto(r, "token")
verificar("login correcto devuelve token", texto(r, "estado") == "OK" and TOKEN, texto(r, "mensaje"))
verificar("login devuelve empleado 0001", texto(r, "codigoEmpleado") == "0001" and texto(r, "nombreEmpleado"))
error_esperado("clave incorrecta -> AUTH-003", operacion("iniciarSesion", usuario="cromero", clave="x"), "AUTH-003")
error_esperado("usuario ANULADO -> AUTH-003", operacion("iniciarSesion", usuario="creyes", clave="linda"), "AUTH-003")
error_esperado("token falso -> AUTH-001", operacion("listarCuentas", token="falso"), "AUTH-001")

print("\n2. Consultas")
cuentas = lista_repetida(operacion("listarCuentas", token=TOKEN), "cuentas")
verificar("lista las 6 cuentas", len(cuentas) == 6, len(cuentas))
r = operacion("consultarMovimientos", token=TOKEN, cuenta="00200001")
movs = lista_repetida(r, "movimientos")
verificar("movimientos de 00200001 en Soles", texto(r, "moneda") == "Soles" and len(movs) >= 15, texto(r, "mensaje"))
verificar("fecha yyyy-MM-dd", texto(movs[0], "fecha") == "2008-01-05", texto(movs[0], "fecha"))
error_esperado("cuenta inexistente -> CTA-001", operacion("consultarMovimientos", token=TOKEN, cuenta="99999999"), "CTA-001")

print("\n3. Deposito")
antes, n = saldo(TOKEN, "00100001")
r = operacion("registrarDeposito", token=TOKEN, cuenta="00100001", importe="100.00")
verificar("saldo = antes + 98.00", texto(r, "estado") == "OK" and Decimal(texto(r, "saldo")) == antes + Decimal("98.00"), texto(r, "mensaje"))
verificar("se registraron 2 movimientos", saldo(TOKEN, "00100001")[1] == n + 2)
error_esperado("importe 0 -> DATA-001", operacion("registrarDeposito", token=TOKEN, cuenta="00100001", importe="0"), "DATA-001")
error_esperado("cuenta CANCELADA -> CTA-002", operacion("registrarDeposito", token=TOKEN, cuenta="00300001", importe="10"), "CTA-002")

print("\n4. Retiro")
antes, _ = saldo(TOKEN, "00100001")
r = operacion("registrarRetiro", token=TOKEN, cuenta="00100001", clave="123456", importe="50.00")
verificar("saldo = antes - 52.00", texto(r, "estado") == "OK" and Decimal(texto(r, "saldo")) == antes - Decimal("52.00"), texto(r, "mensaje"))
error_esperado("clave incorrecta -> CTA-003", operacion("registrarRetiro", token=TOKEN, cuenta="00100001", clave="000000", importe="1"), "CTA-003")
error_esperado("saldo insuficiente -> CTA-004", operacion("registrarRetiro", token=TOKEN, cuenta="00100001", clave="123456", importe="9999999"), "CTA-004")

print("\n5. Transferencia")
o1, _ = saldo(TOKEN, "00100001")
d1, _ = saldo(TOKEN, "00200001")
r = operacion("registrarTransferencia", token=TOKEN, cuentaOrigen="00100001", claveOrigen="123456", cuentaDestino="00200001", importe="25.00")
verificar("transferencia OK", texto(r, "estado") == "OK", texto(r, "mensaje"))
verificar("origen -= 27.00 y destino += 23.00",
          saldo(TOKEN, "00100001")[0] == o1 - Decimal("27.00") and saldo(TOKEN, "00200001")[0] == d1 + Decimal("23.00"))
error_esperado("monedas distintas -> CTA-005", operacion("registrarTransferencia", token=TOKEN, cuentaOrigen="00100001", claveOrigen="123456", cuentaDestino="00100002", importe="1"), "CTA-005")
error_esperado("misma cuenta -> CTA-006", operacion("registrarTransferencia", token=TOKEN, cuentaOrigen="00100001", claveOrigen="123456", cuentaDestino="00100001", importe="1"), "CTA-006")

print(f"\nResultado: {total - fallos}/{total} verificaciones correctas")
sys.exit(1 if fallos else 0)
