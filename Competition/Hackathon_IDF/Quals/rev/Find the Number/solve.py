from pwn import *
import sys

BINARY = "chall"
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
        p.sendline(content)

x, p = init()
x.debug((
    # "break *main+122\n"
    # "break *main+141\n"
    "break *main+151\n"
    "break *main+189\n"
    "break *main+217\n"
    "break *main+250\n"
    "break *main+285\n"
))

v7 = [0]*6
v7[0] = 1337
v7[1] = 0
v7[2] = 0
v7[3] = 1332
v7[4] = 5
v7[5] = 1342

x.send(f"{v7[0]} {v7[1]} {v7[2]} {v7[3]} {v7[4]} {v7[5]}".encode())
print(p.recvall(1))