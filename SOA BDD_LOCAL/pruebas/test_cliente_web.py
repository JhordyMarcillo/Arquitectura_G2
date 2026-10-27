"""
Prueba funcional de un cliente web EurekaBank (cualquier TI): inicia sesion, navega
por cuentas y movimientos, registra un deposito, provoca un error y cierra sesion.
Solo usa la biblioteca estandar de Python.

Uso:
    python test_cliente_web.py http://localhost:5180                                        (Razor Pages)
    python test_cliente_web.py http://localhost:8080/CLIWEB_EurekaBank_JAVA_RESTFUL_GR02 java   (JSP)
"""
import http.cookiejar
import re
import sys
import urllib.parse
import urllib.request

BASE = sys.argv[1].rstrip("/") if len(sys.argv) > 1 else "http://localhost:5180"
JAVA = len(sys.argv) > 2 and sys.argv[2] == "java"
RUTA_LOGIN = "/login" if JAVA else "/Login"
CAMPOS = ({"usuario": "usuario", "clave": "clave", "url": "urlServidor", "cuenta": "cuenta", "destino": "cuentaDestino", "importe": "importe"}
          if JAVA else {"usuario": "Usuario", "clave": "Clave", "url": "UrlServidor", "cuenta": "Cuenta", "destino": "CuentaDestino", "importe": "Importe"})
RUTA_OPERACION = "/operacion?tipo={}" if JAVA else "/Operacion/{}"
RUTA_SALIR = "/salir" if JAVA else "/Login?handler=Salir"
navegador = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
fallos = total = 0


def pedir(ruta, datos=None):
    cuerpo = urllib.parse.urlencode(datos).encode() if datos is not None else None
    with navegador.open(BASE + ruta, data=cuerpo, timeout=20) as r:
        return r.geturl(), r.read().decode("utf-8")


def token(html):
    m = re.search(r'name="__RequestVerificationToken" type="hidden" value="([^"]+)"', html)
    return {"__RequestVerificationToken": m.group(1)} if m else {}


def verificar(nombre, condicion, detalle=""):
    global fallos, total
    total += 1
    print(f"  {'OK   ' if condicion else 'FALLA'} {nombre} {'' if condicion else detalle}")
    fallos += 0 if condicion else 1


print(f"Cliente web: {BASE}\n")
url, html = pedir("/")
verificar("sin sesion redirige al login", "Bienvenido" in html, url)

servidor = re.search(r'name="' + CAMPOS["url"] + r'" value="([^"]*)"', html).group(1)
url, html = pedir(RUTA_LOGIN, {**token(html), CAMPOS["usuario"]: "MONSTER", CAMPOS["clave"]: "x", CAMPOS["url"]: servidor})
verificar("clave incorrecta muestra el error", "incorrectos" in html)

url, html = pedir(RUTA_LOGIN, {**token(html), CAMPOS["usuario"]: "MONSTER", CAMPOS["clave"]: "Monster9", CAMPOS["url"]: servidor})
verificar("login MONSTER entra al panel de cuentas", "Listado de cuentas" in html and "Usuario Monster" in html, url)
verificar("lista las cuentas", html.count("etiqueta-estado") == 6)

url, html = pedir("/movimientos?cuenta=00200001")
verificar("movimientos de 00200001", "Saldo disponible" in html and "Apertura de Cuenta" in html)

url, html = pedir("/movimientos?cuenta=99999999")
verificar("cuenta inexistente muestra el error", "no existe" in html)

url, html = pedir(RUTA_OPERACION.format("deposito"))
url, html = pedir(RUTA_OPERACION.format("deposito"), {**token(html), CAMPOS["cuenta"]: "00200001", CAMPOS["importe"]: "20.00"})
verificar("deposito con comprobante", "Operacion exitosa" in html and "Nuevo saldo" in html)

url, html = pedir(RUTA_OPERACION.format("retiro"))
url, html = pedir(RUTA_OPERACION.format("retiro"), {**token(html), CAMPOS["cuenta"]: "00200001", CAMPOS["clave"]: "000000", CAMPOS["importe"]: "5"})
verificar("retiro con clave incorrecta rechazado (CTA-003)", "Operacion rechazada" in html and "CTA-003" in html)

url, html = pedir(RUTA_OPERACION.format("transferencia"))
url, html = pedir(RUTA_OPERACION.format("transferencia"), {**token(html), CAMPOS["cuenta"]: "00200001", CAMPOS["clave"]: "123456", CAMPOS["destino"]: "00100001", CAMPOS["importe"]: "10"})
verificar("transferencia con comprobante", "Operacion exitosa" in html and "Cuenta de destino" in html)

url, html = pedir("/")
url, html = pedir(RUTA_SALIR, token(html))
verificar("cerrar sesion vuelve al login", "Bienvenido" in html)
url, html = pedir("/movimientos")
verificar("despues de salir no hay acceso", "Bienvenido" in html)

print(f"\nResultado: {total - fallos}/{total} verificaciones correctas")
sys.exit(1 if fallos else 0)
