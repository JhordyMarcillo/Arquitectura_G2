"""Configuracion compartida - Grupo 02 (German Caceres, David Cepeda, Jhordy Marcillo).

Los datos sensibles se leen de variables de entorno (no se guardan en el codigo):
  WEBEX_TOKEN         token personal de https://developer.webex.com (dura 12 h)
  WEBEX_ROOM_ID       id de la sala (salida de create-rooms.py / list-rooms.py)
  WEBEX_PERSON_EMAIL  correo de la persona a consultar / agregar a la sala
"""
import os
import sys


def _req(nombre):
    valor = os.environ.get(nombre)
    if not valor:
        sys.exit("Falta la variable de entorno {}. Ej. PowerShell: $env:{}='...'".format(nombre, nombre))
    return valor


def token():
    return _req("WEBEX_TOKEN")


def headers():
    return {"Authorization": "Bearer {}".format(token()), "Content-Type": "application/json"}


def room_id():
    return _req("WEBEX_ROOM_ID")


def person_email():
    return _req("WEBEX_PERSON_EMAIL")