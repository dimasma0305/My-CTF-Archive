from binascii import hexlify
import requests

URL = "http://0.0.0.0:8000/"


class API:
    def __init__(self, url=URL):
        self.url = url
        self.req = requests.Session()
        self.req.cookies.set("sessionId", "10f997e4-e817-487c-afe5-f1bd2843a025", domain="http://0.0.0.0")
        # print(self.req.get(url).text)

    def index(self):
        res = self.req.get(self.url)
        return res.text

    def get_note(self, title):
        res = self.req.get(f"{self.url}/notes", params={
            "title": title,
        })
        return res.text

    def create_note(self, title, content):
        res = self.req.post(f"{self.url}/notes", data={
            "title": title,
            "content": content,
        }, cookies={
            "sessionId": "10f997e4-e817-487c-afe5-f1bd2843a025"
        })
        return res.text


    
api = API()

print(api.create_note(title=bytes.fromhex("f45c2dca3b0016918d559bf1f36ada242059ae87b9cff6f81b87b0aadc7e"), content="asd"))