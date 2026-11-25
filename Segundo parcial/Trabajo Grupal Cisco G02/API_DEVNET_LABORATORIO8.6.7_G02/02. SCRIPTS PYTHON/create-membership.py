import json
import requests
import config

params = {'roomId': config.room_id(), 'personEmail': config.person_email()}
res = requests.post('https://webexapis.com/v1/memberships', headers=config.headers(), json=params)
print(json.dumps(res.json(), indent=4))