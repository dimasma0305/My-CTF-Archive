from pwn import *
import sys

context.log_level = "INFO"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process("ftp ", shell=True)
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
p.sendline(b"CMD HELP\r\n")
print(p.recv(400))
p.interactive()