import requests

url = "http://103.189.235.186:10007"
def req():
    res = requests.post(f"{url}/ping",json={
        "message":"ping"
    })
    print(res.text)
    
req()