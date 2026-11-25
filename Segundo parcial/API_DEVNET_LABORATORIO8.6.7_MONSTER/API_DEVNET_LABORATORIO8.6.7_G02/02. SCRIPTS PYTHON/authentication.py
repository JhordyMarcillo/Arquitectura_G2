import json
import requests
import config

res = requests.get('https://webexapis.com/v1/people/me', headers=config.headers())
print(res.status_code)
print(json.dumps(res.json(), indent=4))