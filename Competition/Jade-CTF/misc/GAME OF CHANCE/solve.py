from pwn import *


# p = process('python3 chall.py', shell=True)
p = remote('34.76.206.46', 10011)

p.sendlineafter(b"What is your name: ", b"dimas")
p.sendlineafter(b"How old are you: ", b"10")

for i in range(32):
    print(p.recvline())
    sleep(1)
    p.sendlineafter(b">", b"1")

p.interactive()
