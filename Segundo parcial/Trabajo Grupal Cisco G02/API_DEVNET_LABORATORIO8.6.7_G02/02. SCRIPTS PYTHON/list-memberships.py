import json
import requests
import config

res = requests.get('https://webexapis.com/v1/memberships', headers=config.headers(),
                   params={'roomId': config.room_id()})
print(json.dumps(res.json(), indent=4))