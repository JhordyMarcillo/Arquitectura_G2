"""Envia un mensaje (Markdown) a la sala compartida del grupo.

Uso:
    python send-message.py "Hola equipo, ya quedo la parte 5"

Cada integrante lo ejecuta con SU propio WEBEX_TOKEN, asi en la sala se ve quien escribio.
"""
import sys
import requests
import config

if len(sys.argv) < 2 or not sys.argv[1].strip():
    sys.exit('Uso: python send-message.py "tu mensaje"')

yo = requests.get("https://webexapis.com/v1/people/me", headers=config.headers())
if yo.status_code != 200:
    sys.exit("El token no sirve (HTTP {}). Genera uno nuevo en developer.webex.com (dura 12 h).".format(yo.status_code))

res = requests.post("https://webexapis.com/v1/messages", headers=config.headers(),
                    json={"roomId": config.room_id(), "markdown": sys.argv[1]})
if res.status_code == 200:
    print("Mensaje enviado como {}.".format(yo.json().get("displayName")))
else:
    print("Error HTTP {}: {}".format(res.status_code, res.text))
