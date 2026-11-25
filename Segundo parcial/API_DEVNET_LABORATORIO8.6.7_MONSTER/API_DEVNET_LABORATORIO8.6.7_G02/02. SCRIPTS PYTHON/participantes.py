"""Participantes de la sala de Webex del Grupo 02.

Cada integrante debe escribir AQUI el correo con el que tiene (o creara) su cuenta de Webex.
Mientras un correo contenga la palabra PENDIENTE, los scripts lo omiten.
"""

PARTICIPANTES = [
    {"nombre": "David Cepeda", "correo": "davidcepeda751@gmail.com"},
    {"nombre": "German Caceres", "correo": "PENDIENTE-correo-de-German"},
    {"nombre": "Jhordy Marcillo", "correo": "PENDIENTE-correo-de-Jhordy"},
]


def es_valido(correo):
    return "@" in correo and "PENDIENTE" not in correo.upper()
