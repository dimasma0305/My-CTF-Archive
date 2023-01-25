from pwn import *
from struct import pack

# reference https://0xrick.github.io/pwn/collision/
payload = flat(
    pack("<I", 0x6c5cec8)*4,
    0x6c5cecc,
)
r = ssh('col' ,'pwnable.kr' ,password='guest', port=2222)
p: process = r.process(executable='./col', argv=['col',payload])
flag = p.recv()
log.success("Flag: " + flag.decode())
p.close()
r.close()