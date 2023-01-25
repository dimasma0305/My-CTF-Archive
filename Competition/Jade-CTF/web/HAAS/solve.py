import requests

URL = "http://103.20.235.21:10025/dashboard.php"

res = requests.post(URL, data={
    "secret": "SecretZ"
})
print(res.text)