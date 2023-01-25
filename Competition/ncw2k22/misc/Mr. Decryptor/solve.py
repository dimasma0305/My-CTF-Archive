from pwn import *
import sys
from Crypto.Util.number import long_to_bytes

def init():
    p = remote(sys.argv[1], sys.argv[2])
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def start(self):
        p = self.p
        p.recvuntil(b"here we go:\n")

def main():
    x, p = init()
    x.start()
    for _ in range(100):
        val = p.recvline().decode()
        print(val)
        if val.startswith("0x"):
            a = long_to_bytes(eval(val))
            p.sendline(a)
        elif val.startswith("0b"):
            a = long_to_bytes(eval(val))
            p.sendline(a)
        else:
            a = base64.b64decode(val)
            p.sendline(a)
        print(a)
    p.interactive()
    
main()