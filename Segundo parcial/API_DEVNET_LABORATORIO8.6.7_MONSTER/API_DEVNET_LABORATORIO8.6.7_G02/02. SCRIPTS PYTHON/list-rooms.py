import json
import requests
import config

res = requests.get('https://webexapis.com/v1/rooms', headers=config.headers(), params={'max': '100'})
print(json.dumps(res.json(), indent=4))