import os
import random

def createNull(txt):
    '''
    push 0 to stack
    '''
    n = ((len(txt))//4) + 1
    instruction = "\tpush 0\n" * n
    return instruction.strip()


def push(txt):
    '''
    push a text to stack byte by byte using mov and add instruction
    '''
    template = """
    mov al, %d
    add [edi], al
    inc edi\n
    """
    asm = """
    mov edi, esp
    """
    for i in txt:
        asm += (template % ord(i)).rstrip()
    return asm


def buildNasm(txt):
    instruction = f"""
global _start
section .text
_start:
    {createNull(txt)}
    {push(txt)}
    mov eax, 1
    int 0x80"""
    return instruction


with open("chall.asm", "w") as f:
    f.write(buildNasm("dimas maulana"))
os.system("nasm -f elf32 chall.asm -o chall.o && ld chall.o -o chall -m elf_i386")
