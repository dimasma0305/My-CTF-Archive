from pwn import *
import sys

BINARY = "chall"
ld = ELF("ld-linux-aarch64.so.1", checksec=False)
libc = ELF("libc.so.6", checksec=False)
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "aarch64"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process(env={"LD_PRELOAD": "./libc.so.6 ./ld-linux-aarch64.so.1"})
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
        p.sendline(content)

x, p = init()
x.debug()
# x.send(cyclic(100))

p.interactive()