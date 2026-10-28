from flask import Flask, render_template, request, redirect, url_for, session, flash, jsonify
import os
import socket
import requests
from functools import wraps

app = Flask(__name__)
app.secret_key = 'monster_secret_key_2025'


# Configuración del servidor .NET. Se puede fijar con la variable CONUNI_URL;
# si no, usa la red compartida (192.168.137.1) y, si ahi no responde nadie,
# el servidor de la misma PC (localhost).
def elegir_url():
    try:
        with socket.create_connection(('192.168.137.1', 5014), timeout=1.5):
            return 'http://192.168.137.1:5014/api/conversor'
    except OSError:
        return 'http://localhost:5014/api/conversor'


API_BASE_URL = os.environ.get('CONUNI_URL') or elegir_url()

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
    # 0.0.0.0: accesible desde otros equipos y el celular de la misma red
    app.run(debug=True, host='0.0.0.0', port=3000)
