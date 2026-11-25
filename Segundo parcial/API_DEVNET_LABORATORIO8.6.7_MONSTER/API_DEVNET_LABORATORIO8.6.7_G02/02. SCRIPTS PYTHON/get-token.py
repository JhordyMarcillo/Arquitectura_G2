"""Cambia el 'code' de OAuth por un access token de Webex.

Variables de entorno necesarias:
  WEBEX_CLIENT_ID, WEBEX_CLIENT_SECRET, WEBEX_CODE
  WEBEX_REDIRECT_URI (opcional, por defecto http://localhost)
"""
import json
import os
import sys
import requests

try:
    data = {
        "grant_type": "authorization_code",
        "client_id": os.environ["WEBEX_CLIENT_ID"],
        "client_secret": os.environ["WEBEX_CLIENT_SECRET"],
        "code": os.environ["WEBEX_CODE"],
        "redirect_uri": os.environ.get("WEBEX_REDIRECT_URI", "http://localhost"),
    }
except KeyError as e:
    sys.exit("Falta la variable de entorno {}".format(e))

res = requests.post("https://webexapis.com/v1/access_token", data=data)
body = res.json()
if res.status_code != 200:
    sys.exit("Error {}: {}".format(res.status_code, json.dumps(body, indent=2)))

print("access_token (usalo como WEBEX_TOKEN):")
print(body["access_token"])
print("expira en {} s".format(body.get("expires_in")))
