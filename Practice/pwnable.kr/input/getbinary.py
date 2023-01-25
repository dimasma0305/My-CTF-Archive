from pwn import *

r = ssh('input2' ,'pwnable.kr' ,password='guest', port=2222)

def get_file(filename):
    p: process = r.process(argv=['cat',filename])
    with open(filename, "wb") as f:
        f.write(p.recvall())

get_file('input')
get_file('input.c')