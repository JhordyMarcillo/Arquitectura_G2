"""Pide un serviceTicket nuevo al controlador (Packet Tracer, puerto 58000)."""
import json
import requests

BASE = "http://localhost:58000/api/v1"


def get_ticket():
    body = {"username": "cisco", "password": "cisco123!"}
    resp = requests.post(BASE + "/ticket", json.dumps(body),
                         headers={"content-type": "application/json"}, verify=False)
    resp.raise_for_status()
    return resp.json()["response"]["serviceTicket"]