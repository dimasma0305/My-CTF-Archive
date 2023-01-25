#!/usr/bin/env python3

from pwn import *

exe = ELF("./vuln", checksec=False)

context.binary = exe
context.log_level = "CRITICAL"
context.terminal = ["konsole", "-e"]

rop = ROP(exe)

print(rop.find_gadget(["pop rdi", "ret"]))