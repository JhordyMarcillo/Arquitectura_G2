import json
import requests
from ticket import BASE, get_ticket

headers = {"X-Auth-Token": get_ticket()}
resp = requests.get(BASE + "/host", headers=headers, verify=False)
print("Request status: ", resp.status_code)
print(json.dumps(resp.json()["response"], indent=4))