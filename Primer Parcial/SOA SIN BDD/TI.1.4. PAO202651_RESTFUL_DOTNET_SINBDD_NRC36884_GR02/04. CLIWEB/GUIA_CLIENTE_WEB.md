# Guía del cliente web (Python + Flask) — Conversión de Unidades REST

> Cliente: `04. CLIWEB` · Lenguaje: Python 3 + Flask 3 + Jinja2 + Bootstrap 5 · Servidor: `04. SERVIDOR` (ASP.NET Core, `WS_ConUnid_DOTNET_RESTFUL_GR02`)
>
> Esta guía se lee junto con:
> - `04. SERVIDOR/CONTRATO_GR02.md` (el contrato REST que este cliente consume; sale del código del servidor y manda sobre esta guía).
> - `04. SERVIDOR/TALLER CASOS DE USO CONVERSION UNIDADES.docx` (los casos de uso que el cliente debe cumplir).

---

## 0. Instrucciones para quien aplique esta guía (persona o IA)

1. **Lee primero la sección 2** (estado actual). La guía **modifica** un cliente Flask existente (plantillas, imágenes, `requirements.txt`, `iniciar_cliente.bat`): de las plantillas solo trae los cambios. En `04. CLIWEB` de este proyecto ese cliente base **todavía no está**; hay que copiarlo antes de aplicar la sección 5.
2. **Conserva** el diseño de la sección 2.3: plantillas, gradientes, fondo `fondo6.jpg`, logo, animaciones, tarjetas del menú, colores por categoría y mensajes `flash`.
3. Este cliente ya pide la conversión al servidor (no calcula localmente), pero con un contrato que **no existe**: `GET` con parámetros en la URL, sin token y sin categoría. Hay que pasarlo al contrato de la sección 3.
4. No pongas ñ ni tildes en identificadores de código. Los textos visibles sí pueden llevarlas.
5. Si algo choca con el `.docx`, manda el `.docx` en lo funcional y esta guía en la forma. Si choca con el servidor, **manda el servidor** (`CONTRATO_GR02.md`).

| Fuente | Manda en |
|---|---|
| `.docx` del taller | Qué hace el sistema: CU-01…CU-11, flujo, mensajes |
| `04. SERVIDOR/CONTRATO_GR02.md` | Rutas, JSON, códigos, token |
| Esta guía | Cómo queda **este** cliente: rutas Flask, plantillas, estilo |

### Arquitectura de este cliente

```
Navegador  ──HTML──>  Flask (app.py, puerto 3000)  ──JSON + Bearer──>  Servidor .NET (5014)
           <──HTML──                                <──JSON──────────
```

El navegador **nunca** habla con el servidor .NET: lo hace Flask desde Python. Por eso:
- El token se guarda en la `session` de Flask (cookie firmada con `app.secret_key`). Es el "almacenamiento temporal" que pide el taller.
- CORS no interviene (la llamada es de servidor a servidor). El origen `3000` configurado en el servidor .NET no estorba.

---

## 1. Variables del cliente

| Variable | Valor | Dónde |
|---|---|---|
| `API_BASE_URL` | `http://192.168.137.1:5014/api/conversor` | `app.py` |
| Prueba en la misma PC | `http://localhost:5014/api/conversor` | `API_BASE_URL` |
| Puerto del cliente | `3000` | `app.run(port=3000)` |
| Usuario de prueba | `MONSTER` / `Monster9` | Lo valida **el servidor** |
| Timeout HTTP | 10 s | `llamar_api()` |

---

## 2. Análisis del cliente actual

### 2.1 Archivos

| Archivo | Qué hace hoy | Acción |
|---|---|---|
| `app.py` (267 líneas) | Rutas `/`, `/login`, `/logout`, `/clear`, `/menu`, `/conversion/masa`, `/conversion/longitud`, `/conversion/temperatura`, `/verificar_servidor`; login con credenciales quemadas; 3 rutas casi idénticas que hacen `GET` con `?valor=&unidadOrigen=&unidadDestino=` | **Reescribir** (sección 5.1), conservando las rutas y sus nombres |
| `templates/base.html` | Navbar con gradiente `#667eea → #764ba2`, usuario, botón "Cerrar Sesión", mensajes `flash` animados | Se mantiene |
| `templates/login.html` | Fondo `fondo6.jpg` con overlay, tarjeta con logo circular animado, formulario `usuario` / `password`, pie "RESTFUL- CLWEB \| GRUPO …" | Se mantiene; el pie debe decir `GRUPO 02` (sección 5.2) |
| `templates/menu.html` | 3 tarjetas (Masa, Longitud, Temperatura), estado del servidor que se refresca cada 10 s; muestra `https://localhost:7102` fijo | **Ajustar** (sección 5.3) |
| `templates/conversion_masa.html`, `conversion_longitud.html`, `conversion_temperatura.html` | Misma plantilla con distinto color, icono y un `<select name="tipo_conversion">` con 3 pares fijos | **Ajustar** (sección 5.4): dos `<select>` con las unidades del servidor |
| `static/imagenes/fondo6.jpg`, `logo_login.png` | Imágenes que usan las plantillas | Se mantienen |
| `imagenes/fondo6.jpg`, `logo_login.png` | Copia idéntica de `static/imagenes/`; ninguna plantilla la usa | **Eliminar** la carpeta `imagenes/` de la raíz |
| `test_final.py`, `test_methods.py`, `test_params.py`, `test_server.py` | Prueban variantes de `GET`/`POST` con `tipo`, `deUnidad`, `aUnidad`... | **Eliminar** y reemplazar por `test_contrato.py` (sección 5.5) |
| `requirements.txt` | `Flask==3.0.0`, `requests==2.31.0`, `urllib3==2.1.0` | Se mantiene |
| `iniciar_cliente.bat` | Abre `http://localhost:3000` y ejecuta `python app.py` | Se mantiene |
| `.vscode/settings.json` | Puerto de Live Server | Se mantiene |
| `README.md`, `CONVERSIONES.md`, `CAMBIOS_APLICADOS.md`, `SISTEMA_FUNCIONANDO.md`, `SOLUCION.md` | Hablan de `https://localhost:7102`, `deUnidad`/`aUnidad`, `GET`, libras/onzas, toneladas | **Actualizar** (sección 8.3) |

### 2.2 Funciones de `app.py`

| Función | Hoy | Problema frente al taller | Destino |
|---|---|---|---|
| `USUARIO_VALIDO`, `PASSWORD_VALIDO` | Credenciales en el cliente | Viola CU-01 | **Eliminar** |
| `login_requerido` | Revisa `'usuario' in session` | No hay token | Revisar `'token' in session` |
| `index` | Limpia la sesión y redirige al login | — | Conservar |
| `login` | Compara localmente; guarda `session['usuario']` | Viola CU-01 | `POST /login`, guardar `session['token']` |
| `logout`, `clear_session` | Limpian la sesión | — | Conservar |
| `menu` | Solo renderiza | Categorías fijas | `GET /categorias` (CU-02) |
| `conversion_masa/longitud/temperatura` | Diccionario de 3 pares fijos (`kg-g`, `t-kg`...) + `GET` con query | Contrato inexistente; sin token; `tonelada` no está en el servidor; ante 400/401 muestra `response.text` crudo | Las 3 rutas llaman a `procesar_conversion(...)`: `GET /categorias/{c}/unidades` (CU-03) y `POST` con JSON (CU-04) |
| `verificar_servidor` | `GET` a la raíz (que responde 404) | Funciona, pero de casualidad | `GET /categorias` (sin token responde 401 = vivo) y devolver la URL |

### 2.3 Diseño a conservar

| Elemento | Valor actual |
|---|---|
| Fuente | `'Segoe UI', Tahoma, Geneva, Verdana, sans-serif` |
| Gradiente principal | `linear-gradient(135deg, #667eea 0%, #764ba2 100%)` |
| Colores por categoría | Masa `#667eea`; Longitud `#764ba2` (gradiente invertido); Temperatura `#f093fb → #f5576c` |
| Iconos Font Awesome 6.4 | `fa-weight-hanging`, `fa-ruler-combined`, `fa-thermometer-half`, `fa-calculator`, `fa-exchange-alt`, `fa-arrow-left` |
| Fondo | `static/imagenes/fondo6.jpg` fijo con overlay `rgba(0,0,0,0.4)` (login `0.5`) |
| Tarjetas | `rgba(255,255,255,0.95)`, `border-radius: 15px`, hover que sube 5 px |
| Resultado | `.result-card` con gradiente de la categoría, `.result-value` grande |
| Mensajes | `flash(mensaje, 'success' / 'danger' / 'warning' / 'info')` con animación `slideDown` |
| Estado del servidor | Punto verde/rojo con `pulse`, refresco cada 10 s |

### 2.4 Resumen de lo que viola el taller hoy

| Requisito del `.docx` | Hoy | Debe quedar |
|---|---|---|
| El cliente no implementa conversión | Cumple (ya pide al servidor) | Igual |
| CU-01: el servidor autentica y emite token | Credenciales quemadas | `POST /login`, `session['token']` |
| CU-02/CU-03: catálogo del servidor | Pares fijos en `app.py` y en las plantillas | `GET /categorias`, `GET /categorias/{c}/unidades` |
| CU-04: contrato de conversión | `GET ?valor=&unidadOrigen=&unidadDestino=` | `POST` con `{categoria, unidadOrigen, unidadDestino, valor}` |
| CU-05: cada petición lleva token | No | `Authorization: Bearer <token>` |
| CU-08/CU-09 | No existen | Limpiar sesión, `flash` y redirigir al login |
| CU-10/CU-11 | Muestra `Error del servidor: 400 - {json crudo}` | `flash('[UNIT-001] Unidad de origen no soportada: ...', 'danger')` |

---

## 3. Contrato del servidor (resumen para este cliente)

Resumen de `04. SERVIDOR/CONTRATO_GR02.md`; ante cualquier duda manda ese archivo.

| Dato | Valor |
|---|---|
| `API_BASE_URL` (red compartida) | `http://192.168.137.1:5014/api/conversor` |
| `API_BASE_URL` (misma PC) | `http://localhost:5014/api/conversor` |
| Perfil del servidor | `http` (`http://0.0.0.0:5014`, `launchSettings.json`) |
| Prefijo | `api/conversor` (`[Route("api/conversor")]`) |

| CU | Método y ruta | Token | Cuerpo | Respuesta OK |
|---|---|---|---|---|
| CU-01 | `POST /login` | No | `{"usuario":"MONSTER","contrasenia":"Monster9"}` | `{"estado":"OK","codigo":"200","mensaje":"Autenticacion realizada correctamente","token":"<guid>","expiracion":"2026-10-25T20:50:31.060353-05:00"}` |
| CU-02 | `GET /categorias` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Categorias consultadas correctamente","categorias":["LONGITUD","MASA","TEMPERATURA"]}` |
| CU-03 | `GET /categorias/{categoria}/unidades` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Unidades consultadas correctamente","categoria":"MASA","unidades":["MILIGRAMO","GRAMO","KILOGRAMO","LIBRA"]}` |
| CU-04 | `POST /convertir` | Sí | `{"categoria":"MASA","unidadOrigen":"KILOGRAMO","unidadDestino":"GRAMO","valor":100}` | `{"estado":"OK","codigo":"200","mensaje":"Conversion realizada correctamente","resultado":100000,"unidadOrigen":"KILOGRAMO","unidadDestino":"GRAMO"}` |

Header del token: `Authorization: Bearer <token>`. El token dura 30 minutos y vive en memoria del servidor: si el servidor se reinicia, el token deja de valer (`AUTH-001`). No hay endpoint de logout.

| Categoría | Unidades |
|---|---|
| `LONGITUD` | `MILIMETRO`, `CENTIMETRO`, `METRO`, `KILOMETRO`, `MILLA`, `PIE` |
| `MASA` | `MILIGRAMO`, `GRAMO`, `KILOGRAMO`, `LIBRA` |
| `TEMPERATURA` | `CELSIUS`, `FAHRENHEIT`, `KELVIN` |

| HTTP | `codigo` | Caso de uso | `mensaje` del servidor | Qué hace este cliente |
|---|---|---|---|---|
| 401 | `AUTH-001` | CU-08 token inválido | `Token invalido o sesion expirada` | `session.clear()`, `flash(mensaje, 'warning')`, redirige a `/login` |
| 401 | `AUTH-002` | CU-09 sesión expirada | `Sesion expirada, vuelva a autenticarse` | Igual que AUTH-001 |
| 401 | `AUTH-003` | CU-01 credenciales incorrectas | `Credenciales incorrectas` | `flash('Credenciales incorrectas', 'danger')` en el login |
| 400 | `UNIT-001` | CU-10 unidad no soportada | `Unidad de origen no soportada: TONELADA` | `flash('[UNIT-001] ...', 'danger')` en la misma página |
| 400 | `DATA-001` | CU-11 datos inconsistentes | p. ej. `La unidad de origen METRO no pertenece a la categoria MASA` | `flash('[DATA-001] ...', 'danger')` |
| 500 | `SRV-001` | Error interno | `Error interno del servidor` | `flash('[SRV-001] ...', 'danger')` |
| — | `RED` (propio del cliente) | Servidor inalcanzable | — | `flash('Error de conexión: ...', 'danger')` |

`POST /login` con usuario o contraseña vacíos responde `400 DATA-001` (`El usuario y la contrasenia son obligatorios`); `login()` lo muestra con `mensaje_error`.

> La raíz del servidor responde 404 sin cuerpo. Para "verificar servidor" usa `GET /categorias`: sin token responde 401 con JSON, lo que prueba que está vivo.

---

## 4. Estructura objetivo

```
04. CLIWEB/
├── app.py                         # rutas Flask + llamar_api (reescrito)
├── requirements.txt               # igual
├── iniciar_cliente.bat            # igual
├── test_contrato.py               # nuevo: reemplaza los 4 test_*.py
├── templates/
│   ├── base.html                  # igual
│   ├── login.html                 # igual
│   ├── menu.html                  # tarjetas segun /categorias + URL real
│   ├── conversion_masa.html       # dos <select> con unidades del servidor
│   ├── conversion_longitud.html   # idem
│   └── conversion_temperatura.html# idem
├── static/imagenes/fondo6.jpg, logo_login.png
└── GUIA_CLIENTE_WEB.md
```

| Pieza de `app.py` | Responsabilidad |
|---|---|
| `llamar_api` | Único punto que hace HTTP; agrega el token de la sesión; devuelve siempre un `dict` con `estado/codigo/mensaje`; lanza `SesionExpirada` ante AUTH-001/AUTH-002 |
| `@app.errorhandler(SesionExpirada)` | CU-08/CU-09: limpia la sesión y vuelve al login |
| `login` | CU-01 |
| `menu` | CU-02 |
| `procesar_conversion` | CU-03 (GET) y CU-04 (POST) para las 3 rutas de conversión |
| `formatear_numero`, `SIMBOLOS` | Presentación (no convierte nada) |

---

## 5. Plantillas de código

### 5.1 `app.py` (completo)

```python
from flask import Flask, render_template, request, redirect, url_for, session, flash, jsonify
import requests
from functools import wraps

app = Flask(__name__)
app.secret_key = 'monster_secret_key_2025'

# Configuración del servidor .NET
API_BASE_URL = 'http://192.168.137.1:5014/api/conversor'

# Codigos que devuelve el servidor (tabla 4 de 04. SERVIDOR/CONTRATO_GR02.md)
TOKEN_INVALIDO = 'AUTH-001'
SESION_EXPIRADA = 'AUTH-002'
CREDENCIALES_INCORRECTAS = 'AUTH-003'
ERROR_RED = 'RED'  # propio del cliente: el servidor no respondio

# Solo presentacion: simbolo que se muestra junto al resultado
SIMBOLOS = {
    'MILIMETRO': 'mm', 'CENTIMETRO': 'cm', 'METRO': 'm', 'KILOMETRO': 'km', 'MILLA': 'mi', 'PIE': 'ft',
    'MILIGRAMO': 'mg', 'GRAMO': 'g', 'KILOGRAMO': 'kg', 'LIBRA': 'lb',
    'CELSIUS': '°C', 'FAHRENHEIT': '°F', 'KELVIN': 'K'
}


class SesionExpirada(Exception):
    """El servidor respondio AUTH-001 o AUTH-002"""

    def __init__(self, mensaje):
        super().__init__(mensaje)
        self.mensaje = mensaje


def llamar_api(metodo, ruta='', cuerpo=None, con_token=True):
    """Llama al servidor .NET y devuelve siempre un dict con estado, codigo y mensaje"""
    headers = {'Accept': 'application/json'}
    if con_token and 'token' in session:
        headers['Authorization'] = f"Bearer {session['token']}"

    try:
        response = requests.request(metodo, API_BASE_URL + ruta, json=cuerpo,
                                    headers=headers, verify=False, timeout=10)
    except requests.exceptions.RequestException as e:
        return {'estado': 'ERROR', 'codigo': ERROR_RED, 'mensaje': f'Error de conexión: {str(e)}'}

    try:
        datos = response.json()
    except ValueError:
        datos = {'estado': 'ERROR', 'codigo': str(response.status_code),
                 'mensaje': f'Respuesta inesperada del servidor (HTTP {response.status_code})'}

    # CU-08 / CU-09
    if datos.get('codigo') in (TOKEN_INVALIDO, SESION_EXPIRADA):
        raise SesionExpirada(datos.get('mensaje', 'Sesión expirada'))

    return datos


def es_ok(datos):
    return datos.get('estado') == 'OK'


def mensaje_error(datos):
    if datos.get('codigo') == ERROR_RED:
        return datos.get('mensaje')
    return f"[{datos.get('codigo')}] {datos.get('mensaje')}"


def formatear_numero(numero):
    """77.0 -> '77', 0.621371192 -> '0.6214'"""
    texto = f'{numero:.4f}'.rstrip('0').rstrip('.')
    return texto if texto not in ('', '-0') else '0'


@app.errorhandler(SesionExpirada)
def manejar_sesion_expirada(error):
    session.clear()
    flash(f'{error.mensaje}. Vuelva a iniciar sesión.', 'warning')
    return redirect(url_for('login'))


# Decorador para verificar login
def login_requerido(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'token' not in session:
            flash('Debe iniciar sesión primero', 'warning')
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function


# Ruta principal - redirige al login y limpia sesión
@app.route('/')
def index():
    # Siempre limpiar la sesión al inicio
    session.clear()
    return redirect(url_for('login'))


# Ruta de Login (CU-01)
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        usuario = request.form.get('usuario', '').strip()
        password = request.form.get('password', '')

        datos = llamar_api('POST', '/login', {'usuario': usuario, 'contrasenia': password}, con_token=False)

        if es_ok(datos):
            session['usuario'] = usuario
            session['token'] = datos.get('token')
            flash(f'¡Bienvenido {usuario}!', 'success')
            return redirect(url_for('menu'))
        elif datos.get('codigo') == CREDENCIALES_INCORRECTAS:
            flash(datos.get('mensaje', 'Usuario o contraseña incorrectos'), 'danger')
        else:
            flash(mensaje_error(datos), 'danger')

    return render_template('login.html')


# Ruta de Logout (el servidor no tiene logout: solo se olvida el token)
@app.route('/logout')
def logout():
    session.clear()
    flash('Sesión cerrada exitosamente', 'info')
    return redirect(url_for('login'))


# Ruta para limpiar sesión (útil para debugging)
@app.route('/clear')
def clear_session():
    session.clear()
    return redirect(url_for('login'))


# Menú principal de conversiones (CU-02)
@app.route('/menu')
@login_requerido
def menu():
    datos = llamar_api('GET', '/categorias')
    categorias = datos.get('categorias', []) if es_ok(datos) else []
    if not es_ok(datos):
        flash(mensaje_error(datos), 'danger')
    return render_template('menu.html', usuario=session.get('usuario'),
                           categorias=categorias, servidor=API_BASE_URL)


def procesar_conversion(categoria, plantilla):
    """CU-03 (unidades del servidor) y CU-04 (conversion en el servidor)"""
    resultado = None
    error = None

    datos_unidades = llamar_api('GET', f'/categorias/{categoria}/unidades')
    unidades = datos_unidades.get('unidades', []) if es_ok(datos_unidades) else []
    if not es_ok(datos_unidades):
        error = mensaje_error(datos_unidades)
        flash(error, 'danger')

    origen = request.form.get('unidad_origen', '')
    destino = request.form.get('unidad_destino', '')

    if request.method == 'POST' and unidades:
        try:
            valor = float(request.form.get('valor', '').replace(',', '.'))
        except ValueError:
            error = 'Por favor ingrese un valor numérico válido'
            flash(error, 'danger')
        else:
            datos = llamar_api('POST', '/convertir', {
                'categoria': categoria,
                'unidadOrigen': origen,
                'unidadDestino': destino,
                'valor': valor
            })

            if es_ok(datos):
                valor_convertido = formatear_numero(datos['resultado'])
                resultado = {
                    'resultado': f"{valor_convertido} {SIMBOLOS.get(datos['unidadDestino'], '')}",
                    'valorOriginal': formatear_numero(valor),
                    'unidadOrigen': datos['unidadOrigen'].lower(),
                    'valorConvertido': valor_convertido,
                    'unidadDestino': datos['unidadDestino'].lower()
                }
                flash('Conversión realizada exitosamente', 'success')
            else:
                error = mensaje_error(datos)
                flash(error, 'danger')

    return render_template(plantilla, resultado=resultado, error=error, unidades=unidades,
                           origen=origen, destino=destino)


# Conversión de Masa
@app.route('/conversion/masa', methods=['GET', 'POST'])
@login_requerido
def conversion_masa():
    return procesar_conversion('MASA', 'conversion_masa.html')


# Conversión de Longitud
@app.route('/conversion/longitud', methods=['GET', 'POST'])
@login_requerido
def conversion_longitud():
    return procesar_conversion('LONGITUD', 'conversion_longitud.html')


# Conversión de Temperatura
@app.route('/conversion/temperatura', methods=['GET', 'POST'])
@login_requerido
def conversion_temperatura():
    return procesar_conversion('TEMPERATURA', 'conversion_temperatura.html')


# Verificar conexión con el servidor
@app.route('/verificar_servidor')
@login_requerido
def verificar_servidor():
    try:
        # Sin token el servidor responde 401 con JSON: basta para saber que esta vivo
        response = requests.get(f'{API_BASE_URL}/categorias', verify=False, timeout=5)
        return jsonify({
            'estado': 'conectado',
            'codigo': response.status_code,
            'mensaje': 'Servidor .NET activo',
            'url': API_BASE_URL
        })
    except requests.exceptions.RequestException as e:
        return jsonify({
            'estado': 'desconectado',
            'mensaje': f'Error: {str(e)}',
            'url': API_BASE_URL
        }), 500


if __name__ == '__main__':
    import urllib3
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
    app.run(debug=True, port=3000)
```

Notas:
- `procesar_conversion` reemplaza los 3 bloques duplicados; las rutas y sus nombres (`conversion_masa`, ...) no cambian, así `url_for(...)` en las plantillas sigue funcionando.
- `resultado` conserva las mismas claves que hoy (`resultado`, `valorOriginal`, `unidadOrigen`, `valorConvertido`, `unidadDestino`), así la tarjeta de resultado de las plantillas no cambia.
- `SesionExpirada` se lanza desde cualquier ruta y el `errorhandler` la convierte en un redirect. Si `/verificar_servidor` (que llama el JavaScript) no usa `llamar_api`, nunca recibe un redirect HTML.

### 5.2 `base.html` y `login.html`

Sin cambios de lógica. El formulario ya envía `usuario` y `password`, que `login()` lee. Único cambio: el pie del login debe decir `RESTFUL- CLWEB | GRUPO 02`.

### 5.3 `menu.html` (cambios)

1. La URL fija `https://localhost:7102` pasa a ser la real:

```html
<small class="text-muted" id="serverUrl">{{ servidor }}</small>
```

2. Cada tarjeta se muestra solo si el servidor la incluye en `/categorias` (CU-02), y los subtítulos reflejan el catálogo real:

```html
<div class="row justify-content-center">
    {% if 'MASA' in categorias %}
    <div class="col-md-3 col-sm-6 mb-3">
        <a href="{{ url_for('conversion_masa') }}" class="conversion-card masa-card">
            <div class="text-center">
                <div class="conversion-icon">
                    <i class="fas fa-weight-hanging"></i>
                </div>
                <h3>MASA</h3>
                <p>mg | g | kg | lb</p>
            </div>
        </a>
    </div>
    {% endif %}

    {% if 'LONGITUD' in categorias %}
    <div class="col-md-3 col-sm-6 mb-3">
        <a href="{{ url_for('conversion_longitud') }}" class="conversion-card longitud-card">
            <div class="text-center">
                <div class="conversion-icon">
                    <i class="fas fa-ruler-combined"></i>
                </div>
                <h3>LONGITUD</h3>
                <p>mm | cm | m | km | mi | ft</p>
            </div>
        </a>
    </div>
    {% endif %}

    {% if 'TEMPERATURA' in categorias %}
    <div class="col-md-3 col-sm-6 mb-3">
        <a href="{{ url_for('conversion_temperatura') }}" class="conversion-card temperatura-card">
            <div class="text-center">
                <div class="conversion-icon">
                    <i class="fas fa-thermometer-half"></i>
                </div>
                <h3>TEMPERATURA</h3>
                <p>°C | °F | K</p>
            </div>
        </a>
    </div>
    {% endif %}
</div>
```

El CSS y el JavaScript de `verificarServidor()` no cambian (ya leen `data.url`).

### 5.4 Plantillas de conversión (cambio en las 3)

En `conversion_masa.html`, `conversion_longitud.html` y `conversion_temperatura.html`:

1. Reemplaza el bloque `<div class="mb-3">` del `<select ... name="tipo_conversion">` por este (igual en las 3; `request.endpoint` no hace falta porque cada `<form>` ya tiene su `action`):

```html
<div class="row">
    <div class="col-md-6 mb-3">
        <label for="unidad_origen" class="form-label fw-bold">Unidad de origen</label>
        <select class="form-select" id="unidad_origen" name="unidad_origen" required>
            <option value="">Seleccione</option>
            {% for unidad in unidades %}
            <option value="{{ unidad }}" {% if unidad == origen %}selected{% endif %}>{{ unidad|capitalize }}</option>
            {% endfor %}
        </select>
    </div>
    <div class="col-md-6 mb-3">
        <label for="unidad_destino" class="form-label fw-bold">Unidad de destino</label>
        <select class="form-select" id="unidad_destino" name="unidad_destino" required>
            <option value="">Seleccione</option>
            {% for unidad in unidades %}
            <option value="{{ unidad }}" {% if unidad == destino %}selected{% endif %}>{{ unidad|capitalize }}</option>
            {% endfor %}
        </select>
    </div>
</div>
```

2. Cambia el subtítulo `<p class="text-muted">` de cada página:

| Plantilla | Subtítulo nuevo |
|---|---|
| `conversion_masa.html` | `Miligramo, Gramo, Kilogramo, Libra` |
| `conversion_longitud.html` | `Milímetro, Centímetro, Metro, Kilómetro, Milla, Pie` |
| `conversion_temperatura.html` | `Celsius, Fahrenheit, Kelvin` |

3. Para que el valor ingresado no se borre tras convertir, agrega `value="{{ request.form.get('valor', '') }}"` al `<input id="valor">`.

Colores, iconos, botones "Convertir" / "Volver al Menú" y la tarjeta `{% if resultado %}` quedan igual.

### 5.5 `test_contrato.py` (nuevo, reemplaza los 4 `test_*.py`)

```python
"""
Prueba el contrato REST que usa el cliente web
Uso: python test_contrato.py [url_api]
"""
import sys

import requests

URL = sys.argv[1] if len(sys.argv) > 1 else 'http://192.168.137.1:5014/api/conversor'


def llamar(metodo, ruta='', cuerpo=None, token=None):
    headers = {'Authorization': f'Bearer {token}'} if token else {}
    response = requests.request(metodo, URL + ruta, json=cuerpo, headers=headers, timeout=10)
    return response.status_code, response.json()


def comprobar(nombre, condicion):
    print(f"{'✓' if condicion else '✗'} {nombre}")
    return condicion


def main():
    print('=' * 70)
    print(f'PRUEBAS DEL CONTRATO: {URL}')
    print('=' * 70)
    ok = []

    estado, datos = llamar('GET', '/categorias')
    ok.append(comprobar('CU-08 sin token -> 401 AUTH-001', estado == 401 and datos['codigo'] == 'AUTH-001'))

    estado, datos = llamar('POST', '/login', {'usuario': 'MONSTER', 'contrasenia': 'x'})
    ok.append(comprobar('CU-01 clave incorrecta -> 401 AUTH-003', estado == 401 and datos['codigo'] == 'AUTH-003'))

    estado, datos = llamar('POST', '/login', {'usuario': 'MONSTER', 'contrasenia': 'Monster9'})
    token = datos.get('token')
    ok.append(comprobar('CU-01 login -> token', estado == 200 and bool(token)))

    estado, datos = llamar('GET', '/categorias', token=token)
    ok.append(comprobar('CU-02 categorias', datos.get('categorias') == ['LONGITUD', 'MASA', 'TEMPERATURA']))

    for categoria, unidad in [('MASA', 'GRAMO'), ('LONGITUD', 'METRO'), ('TEMPERATURA', 'KELVIN')]:
        estado, datos = llamar('GET', f'/categorias/{categoria}/unidades', token=token)
        ok.append(comprobar(f'CU-03 unidades de {categoria}', unidad in datos.get('unidades', [])))

    casos = [
        ('MASA', 'KILOGRAMO', 'GRAMO', 100, 100000),
        ('LONGITUD', 'KILOMETRO', 'METRO', 5, 5000),
        ('TEMPERATURA', 'CELSIUS', 'FAHRENHEIT', 25, 77),
        ('TEMPERATURA', 'CELSIUS', 'KELVIN', 0, 273.15),
    ]
    for categoria, origen, destino, valor, esperado in casos:
        estado, datos = llamar('POST', '/convertir', {'categoria': categoria, 'unidadOrigen': origen,
                                                      'unidadDestino': destino, 'valor': valor}, token)
        ok.append(comprobar(f'CU-04 {valor} {origen} -> {esperado} {destino}',
                            estado == 200 and abs(datos['resultado'] - esperado) < 1e-9))

    estado, datos = llamar('POST', '/convertir', {'categoria': 'MASA', 'unidadOrigen': 'TONELADA',
                                                  'unidadDestino': 'KILOGRAMO', 'valor': 1}, token)
    ok.append(comprobar('CU-10 TONELADA -> 400 UNIT-001', estado == 400 and datos['codigo'] == 'UNIT-001'))

    estado, datos = llamar('POST', '/convertir', {'categoria': 'MASA', 'unidadOrigen': 'METRO',
                                                  'unidadDestino': 'GRAMO', 'valor': 1}, token)
    ok.append(comprobar('CU-11 METRO en MASA -> 400 DATA-001', estado == 400 and datos['codigo'] == 'DATA-001'))

    print('=' * 70)
    print(f'Resultado: {sum(ok)}/{len(ok)} pruebas correctas')


if __name__ == '__main__':
    main()
```

---

## 6. Casos de uso del taller en este cliente

| CU | Dónde se cumple | Cómo se ve |
|---|---|---|
| CU-01 Autenticarse | `login()` → `POST /login` | Flash `¡Bienvenido MONSTER!` o `Credenciales incorrectas` |
| CU-02 Consultar catálogo | `menu()` → `GET /categorias` | Solo las tarjetas que envía el servidor |
| CU-03 Consultar unidades | `procesar_conversion` (GET) | `<select>` de origen y destino |
| CU-04 Realizar conversión | `procesar_conversion` (POST) | Tarjeta de resultado: `100000 g` / `100 kilogramo = 100000 gramo` |
| CU-05 Validar token | Servidor; Flask envía `Authorization: Bearer` desde `session['token']` | — |
| CU-06 Validar datos | Servidor; el navegador exige campos (`required`, `type="number"`) y Flask revisa el número | Flash `Por favor ingrese un valor numérico válido` |
| CU-07 Generar respuesta | Servidor | Flash `Conversión realizada exitosamente` |
| CU-08 Token inválido | `SesionExpirada` + `errorhandler` | Login con flash `Token invalido o sesion expirada. Vuelva a iniciar sesión.` |
| CU-09 Sesión expirada | Igual con `AUTH-002` | Login con flash `Sesion expirada, vuelva a autenticarse. Vuelva a iniciar sesión.` |
| CU-10 Unidad no soportada | `procesar_conversion` | Flash `[UNIT-001] Unidad de origen no soportada: ...` |
| CU-11 Datos inconsistentes | `procesar_conversion` | Flash `[DATA-001] ...` |

---

## 7. Flujo de páginas

```
/  ──(limpia sesión)──> /login
/login  POST ──> POST /login (servidor) ──OK──> session[token] ──> /menu
/menu        ──> GET /categorias ──> tarjetas
/conversion/<c>  GET  ──> GET /categorias/<C>/unidades ──> formulario
/conversion/<c>  POST ──> POST /convertir (servidor) ──> tarjeta de resultado o flash de error
cualquier ruta + AUTH-001/AUTH-002 ──> session.clear() ──> /login
/logout ──> session.clear() ──> /login
```

---

## 8. Configuración, ejecución y verificación

### 8.1 Ejecutar

```powershell
cd "04. CLIWEB"
pip install -r requirements.txt
python app.py
```
o `iniciar_cliente.bat`, y abrir `http://localhost:3000`. El servidor .NET debe estar corriendo: `dotnet run --launch-profile http` en `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02/WS_ConUnid_DOTNET_RESTFUL_GR02` (escucha en `http://0.0.0.0:5014`). `192.168.137.1` solo existe con el hotspot de Windows encendido; en la misma PC cambia `API_BASE_URL` a `http://localhost:5014/api/conversor`.

### 8.2 Lista de pruebas manuales

| # | Acción | Esperado |
|---|---|---|
| 1 | Servidor apagado, login | Flash `Error de conexión: ...` |
| 2 | Login `MONSTER` / `x` | Flash `Credenciales incorrectas` |
| 3 | Login correcto | Menú con 3 tarjetas y `Conectado ✓` con la URL real |
| 4 | Masa | Selects con Miligramo, Gramo, Kilogramo, Libra (sin Tonelada) |
| 5 | Masa, Kilogramo → Gramo, 100 | Tarjeta `100000 g` y `100 kilogramo = 100000 gramo` |
| 6 | Temperatura, Celsius → Kelvin, 0 | `273.15 K` |
| 7 | Longitud, Kilometro → Milla, 1 | `0.6214 mi` |
| 8 | Reiniciar el servidor .NET y convertir | Redirige al login con `Token invalido o sesion expirada. Vuelva a iniciar sesión.` |
| 9 | Escribir `/menu` sin sesión | `Debe iniciar sesión primero` |
| 10 | Cerrar Sesión | `Sesión cerrada exitosamente` |
| 11 | `python test_contrato.py` | `13/13 pruebas correctas` |

### 8.3 Cambios en la documentación y archivos

- Eliminar `imagenes/` (raíz) y `test_final.py`, `test_methods.py`, `test_params.py`, `test_server.py`.
- `README.md`: URL `http://192.168.137.1:5014`, login validado por el servidor, contrato de la sección 3 (no `tipo`/`deUnidad`/`aUnidad`), catálogo real.
- `CONVERSIONES.md`, `SISTEMA_FUNCIONANDO.md`, `SOLUCION.md`, `CAMBIOS_APLICADOS.md`: actualizarlos con el contrato nuevo o eliminarlos (describen problemas del borrador anterior: 405, `GET` con query, `https://localhost:7102`).

---

## 9. Decisiones tomadas y puntos a confirmar

| # | Punto | Decisión de esta guía |
|---|---|---|
| 1 | Toneladas | El `.docx` no las incluye (masa: miligramo, gramo, kilogramo, libra). "Tonelada ↔ kg" se quita; el catálogo sale del servidor |
| 2 | Cerrar sesión | El `.docx` no lo pide y el servidor no tiene logout. El cliente web ya tenía el botón "Cerrar Sesión" y se conserva (solo limpia la sesión de Flask). Consola y escritorio lo dejan apagado con `PERMITIR_CERRAR_SESION = false`; si se quiere lo mismo aquí, basta con quitar el botón del navbar de `base.html` y dejar la ruta `/logout` |
| 3 | El token vive en la cookie de sesión de Flask (firmada, no cifrada) | Suficiente para el taller ("almacenar temporalmente"); en producción iría del lado servidor |
| 4 | Las tarjetas del menú siguen siendo 3 plantillas | Una categoría nueva en el servidor necesita su ruta y su plantilla aquí; si se quiere 100 % dinámico, se generaliza a `/conversion/<categoria>` con una sola plantilla |
| 5 | Código Python de esta guía | Revisado contra `CONTRATO_GR02.md`. Las 13 respuestas que espera `test_contrato.py` se comprobaron con `curl` contra el servidor GR02 el 2026-10-25, pero el código Python **no se ejecutó** (la PC no tiene Python). Ejecuta la lista 8.2 al aplicarla |
| 6 | Decimales | El `.docx` no lo especifica (solo el ejemplo 5 KM → 5000 M). Extra: `formatear_numero` muestra hasta 4 decimales sin ceros sobrantes; cambia el `.4f` para otro número |
