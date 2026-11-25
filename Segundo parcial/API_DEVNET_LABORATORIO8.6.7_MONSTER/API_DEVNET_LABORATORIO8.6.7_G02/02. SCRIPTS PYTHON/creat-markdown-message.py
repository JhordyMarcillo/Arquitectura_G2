import json
import requests
import config

message = 'Hola **DevNet Associates**! Grupo 02: German Caceres, David Cepeda, Jhordy Marcillo'
params = {'roomId': config.room_id(), 'markdown': message}
res = requests.post('https://webexapis.com/v1/messages', headers=config.headers(), json=params)
print(json.dumps(res.json(), indent=4))