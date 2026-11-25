import requests
from ticket import BASE, get_ticket

headers = {"X-Auth-Token": get_ticket()}
resp = requests.get(BASE + "/network-device", headers=headers, verify=False)
print("Request status: ", resp.status_code)
for d in resp.json()["response"]:
    print(d["hostname"], "\t", d["platformId"], "\t", d["managementIpAddress"])