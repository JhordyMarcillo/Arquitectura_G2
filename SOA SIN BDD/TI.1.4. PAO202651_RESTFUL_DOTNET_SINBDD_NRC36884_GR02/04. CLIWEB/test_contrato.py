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
