from pwn import *

r = ssh('random' ,'pwnable.kr' ,password='guest', port=2222)
p: process = r.process(argv=['random'])

random = 0x6b8b4567
deadbeef = 0xdeadbeef

key = random ^ deadbeef

p.sendline(str(int(key)).encode())

p.interactive()