import requests 

URL = "http://localhost:1337/"

def req(x,url=URL):
    res = requests.post(url, data={
        "url": x,
    },allow_redirects=False)
    return res.text

for i in range(0xffff):
    a = req(f"http://locahost{chr(i)}.ap-1.sharedwithexpose.com/")
    print(a)