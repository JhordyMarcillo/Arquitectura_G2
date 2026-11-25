import json
import requests
import config

params = {'title': 'DevNet Associate Training - Grupo 02'}
res = requests.post('https://webexapis.com/v1/rooms', headers=config.headers(), json=params)
print(json.dumps(res.json(), indent=4))