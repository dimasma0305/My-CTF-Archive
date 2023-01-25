#!/usr/bin/env python3

from base64 import b64encode
from string import ascii_uppercase, ascii_lowercase

def encrypt_1(msg, key):
	encoded = ""

	for i in msg:
		if i.isalpha():
			if i.islower():
				encoded += ascii_lowercase[(ascii_lowercase.find(i) + key) % 26]
			else:
				encoded += ascii_uppercase[(ascii_uppercase.find(i) + key) % 26]
		else:
			encoded += i

	return encoded

def encrypt_2(msg):
	return b64encode(''.join(chr(ord(i)^j) for j,i in enumerate(msg)).encode()).decode()

def main():
	flag = open("flag.txt").read()
	flag = encrypt_1(flag, 22)
	flag = encrypt_2(flag)

	with open('flag.enc', 'w') as w:
		w.write(flag)
		w.close()

if __name__ == '__main__':
	main()
