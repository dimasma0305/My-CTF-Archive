#!/usr/bin/env python3
from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto.Util.Padding import *
import random

FLAG = open("flag.enc", "rb").read()
STEG = b"gemasteg"


def getrandsteg():
    x = list(STEG)
    random.shuffle(x)
    return bytes(x)


def encrypt(msg: bytes, key: bytes):
    key = SHA256.new(key).digest()
    iv = STEG * 2
    aes = AES.new(key, AES.MODE_CBC, iv)
    enc = aes.encrypt(msg)
    return enc


def double(msg: bytes, keys):
    msg = pad(msg, AES.block_size)
    for key in keys:
        msg = encrypt(msg, key)
    return msg


def fwrite(filename: str, data: bytes):
    f = open(filename, "wb")
    f.write(data)
    f.close()


keys = [getrandsteg() for _ in range(2)]
print(keys)
# fwrite("flag.png", double(FLAG, keys))
