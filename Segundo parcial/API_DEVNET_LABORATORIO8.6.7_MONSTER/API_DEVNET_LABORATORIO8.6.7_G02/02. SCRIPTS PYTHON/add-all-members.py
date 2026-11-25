"""Agrega a la sala (WEBEX_ROOM_ID) a todos los participantes de participantes.py.

Lo ejecuta quien creo la sala (el dueño del WEBEX_TOKEN). Es seguro repetirlo:
si alguien ya es miembro, se informa y se sigue con el siguiente.
"""
import requests
import config
from participantes import PARTICIPANTES, es_valido

URL = "https://webexapis.com/v1/memberships"
sala = config.room_id()

for p in PARTICIPANTES:
    nombre, correo = p["nombre"], p["correo"]
    if not es_valido(correo):
        print("OMITIDO  {}: falta su correo en participantes.py".format(nombre))
        continue
    res = requests.post(URL, headers=config.headers(), json={"roomId": sala, "personEmail": correo})
    if res.status_code == 200:
        print("AGREGADO {} <{}>".format(nombre, correo))
    elif res.status_code == 409:
        print("YA ESTABA {} <{}>".format(nombre, correo))
    else:
        try:
            detalle = res.json().get("message", res.text)
        except ValueError:
            detalle = res.text
        print("ERROR {} <{}>: HTTP {} {}".format(nombre, correo, res.status_code, detalle))
