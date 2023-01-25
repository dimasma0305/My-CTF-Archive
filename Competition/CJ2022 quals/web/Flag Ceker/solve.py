from time import sleep
import requests

URL = "http://167.172.80.90:10971/index.php"

def req(pay):
    res = requests.post(URL, data={
        "flag": pay,
    })
    return res.text

for i in range(0xff):
    my_req = req(chr(i))
    print(i)
    if "Flag Salah" not in my_req:
        print(my_req)
    sleep(0.25)