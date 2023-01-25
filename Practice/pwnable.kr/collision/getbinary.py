from pwn import *

r = ssh('col' ,'pwnable.kr' ,password='guest', port=2222)

def get_file(filename):
    p: process = r.process(executable='cat', argv=['cat',filename])
    with open(filename, "wb") as f:
        f.write(p.recvall())

get_file('col')
get_file('col.c')