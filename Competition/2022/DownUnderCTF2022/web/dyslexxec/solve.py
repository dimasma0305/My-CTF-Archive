import requests

URL = "https://web-dyslexxec-773a3cb4c483.2022.ductf.dev"

req = requests.post(
    url=f"{URL}/metadata",
    files={
        "file": open("fizzbuzz/fizzbuzz.xlsm", "rb")
    },
    # proxies={"https":"http://localhost:8080"},
    verify=False
)
print(req.text)