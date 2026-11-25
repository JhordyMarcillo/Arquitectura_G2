import json
import requests
import config

res = requests.get('https://webexapis.com/v1/people', headers=config.headers(),
                   params={'email': config.person_email()})
print(json.dumps(res.json(), indent=4))