from pwn import *

conn = remote('be.ax', 31800)

p = 2**521 - 1
a = int(conn.recvline().decode().strip().split('a = ')[1])
b = int(conn.recvline().decode().strip().split('b = ')[1])
x = y = -b * pow(a - 1, -1, p) % p
conn.sendlineafter(b'starting point: ', str(x).encode())
conn.sendlineafter(b'guess? ', str(y).encode())

print(conn.recvline().decode())

# corctf{r34l_psych1c5_d0nt_n33d_f1x3d_p01nt5_t0_tr1ck_th15_lcg!}