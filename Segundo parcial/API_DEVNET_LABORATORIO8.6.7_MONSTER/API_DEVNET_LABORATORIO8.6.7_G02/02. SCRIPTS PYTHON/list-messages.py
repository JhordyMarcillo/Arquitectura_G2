"""Muestra en la consola los mensajes de la sala (WEBEX_ROOM_ID).

Uso:
    python list-messages.py            # ultimos 20 mensajes
    python list-messages.py 50         # ultimos 50
    python list-messages.py --seguir   # queda escuchando y muestra los nuevos (Ctrl+C para salir)
"""
import sys
import time
from datetime import datetime
import requests
import config

BASE = "https://webexapis.com/v1"


def nombres_de_la_sala(sala):
    """correo -> nombre, tomado de las membresias de la sala."""
    res = requests.get(BASE + "/memberships", headers=config.headers(), params={"roomId": sala, "max": 100})
    if res.status_code != 200:
        return {}
    return {m.get("personEmail"): m.get("personDisplayName") for m in res.json().get("items", [])}


def traer(sala, maximo):
    res = requests.get(BASE + "/messages", headers=config.headers(), params={"roomId": sala, "max": maximo})
    if res.status_code != 200:
        sys.exit("Error HTTP {}: {}".format(res.status_code, res.text))
    return list(reversed(res.json().get("items", [])))   # la API los entrega del mas nuevo al mas viejo


def mostrar(m, nombres):
    fecha = m.get("created", "")
    try:
        fecha = datetime.fromisoformat(fecha.replace("Z", "+00:00")).astimezone().strftime("%d/%m/%Y %H:%M:%S")
    except ValueError:
        pass
    quien = nombres.get(m.get("personEmail")) or m.get("personEmail", "?")
    print("[{}] {}: {}".format(fecha, quien, m.get("text", "(sin texto)")))


if __name__ == "__main__":
    sala = config.room_id()
    seguir = "--seguir" in sys.argv
    numeros = [a for a in sys.argv[1:] if a.isdigit()]
    maximo = int(numeros[0]) if numeros else 20

    nombres = nombres_de_la_sala(sala)
    vistos = set()
    for m in traer(sala, maximo):
        vistos.add(m["id"])
        mostrar(m, nombres)

    if seguir:
        print("--- escuchando mensajes nuevos (Ctrl+C para salir) ---")
        try:
            while True:
                time.sleep(5)
                for m in traer(sala, 10):
                    if m["id"] not in vistos:
                        vistos.add(m["id"])
                        nombres = nombres_de_la_sala(sala)
                        mostrar(m, nombres)
        except KeyboardInterrupt:
            print("\nFin.")
