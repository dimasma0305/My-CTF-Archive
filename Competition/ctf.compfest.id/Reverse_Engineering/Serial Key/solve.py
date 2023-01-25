from string import ascii_uppercase as up, digits as num
import random
from pwn import *

p = process("./soal")

def con(data):
    p.sendlineafter(b"> ", data.encode())

sample = up + num
for i in range(100):
    x = list(sample)
    data = x
    random.shuffle(data)
    for i in range(4,25,5):
        data[i] = "-"
    print("".join(data[:24]))
    con(''.join(data)[:24])

p.interactive()