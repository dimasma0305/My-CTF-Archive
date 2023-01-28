import requests

URL = "http://10.129.32.51:8080/search"

for i in range(0xff):
    r = requests.post(URL, data={'name': chr(i)})
    print(chr(i), i)
    print(len(r.text))