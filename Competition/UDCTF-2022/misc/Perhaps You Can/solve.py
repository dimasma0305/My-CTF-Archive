from z3 import *

s = Solver()
flag = [BitVec(f"flag_{i}", 32) for i in range(0, 48)]


s.add(flag[0] == ord('U'))
s.add(flag[1] == ord('D'))
s.add(flag[2] == ord('C'))
s.add(flag[3] == ord('T'))
s.add(flag[4] == ord('F'))
s.add(flag[5] == ord('{'))

s.add(flag[0x0C] == flag[0x9])
s.add(flag[0x2B] == flag[0x2E])

s.add(flag[0x15] ^ flag[0x18] == 0x6C)
s.add(flag[0x23] ^ flag[0x26] == 0x0A)
s.add(flag[0x0A] ^ flag[0x0D] == 0x0A)
s.add(flag[0x19] ^ flag[0x1C] == 0x5C)
s.add(flag[0x6] ^ flag[0x9] == 0x6B)
s.add(flag[0x0C] ^ flag[0x0F] == 0x41)
s.add(flag[0x27] ^ flag[0x2A] == 0x54)
s.add(flag[0x2F] ^ flag[0x2] == 0x3E)
s.add(flag[0x2E] ^ flag[0x1] == 0x23)
s.add(flag[0x24] ^ flag[0x27] == 0x47)
s.add(flag[0x0F] ^ flag[0x12] == 0x6)
s.add(flag[0x0D] ^ flag[0x10] == 0x37)
s.add(flag[0x14] ^ flag[0x17] == 0x0C)
s.add(flag[0x16] ^ flag[0x19] == 0x59)
s.add(flag[0x13] ^ flag[0x16] == 0x6)
s.add(flag[0x20] ^ flag[0x23] == 0x48)
s.add(flag[0x00] ^ flag[0x2D] == 0x3B)
s.add(flag[0x1D] ^ flag[0x20] == 0x42)
s.add(flag[0x2C] ^ flag[0x2F] == 0x4C)
s.add(flag[0x1E] ^ flag[0x21] == 0x6C)
s.add(flag[0x5] ^ flag[0x8] == 0x29)
s.add(flag[0x1B] ^ flag[0x1E] == 0x70)
s.add(flag[0x1A] ^ flag[0x1D] == 0x55)
s.add(flag[0x1C] ^ flag[0x1F] == 0x6F)
s.add(flag[0x10] ^ flag[0x13] == 0x6A)
s.add(flag[0x29] ^ flag[0x2C] == 0x44)
s.add(flag[0x25] ^ flag[0x28] == 0x53)
s.add(flag[0x22] ^ flag[0x25] == 0x5)
s.add(flag[0x26] ^ flag[0x29] == 0x11)
s.add(flag[0x2] ^ flag[0x5] == 0x38)
s.add(flag[0x17] ^ flag[0x1A] == 0x59)
s.add(flag[0x18] ^ flag[0x1B] == 0x70)
s.add(flag[0x8] ^ flag[0x0B] == 0x0D)
s.add(flag[0x21] ^ flag[0x24] == 0x2B)
s.add(flag[0x1F] ^ flag[0x22] == 0x6B)
s.add(flag[0x28] ^ flag[0x2B] == 0x5)
s.add(flag[0x0E] ^ flag[0x11] == 0x33)
s.add(flag[0x7] ^ flag[0x0A] == 0x54)
s.add(flag[0x11] ^ flag[0x14] == 0x14)
s.add(flag[0x3] ^ flag[0x6] == 0x0C)
s.add(flag[0x4] ^ flag[0x7] == 0x76)
s.add(flag[0x2A] ^ flag[0x2D] == 0x9)
s.add(flag[0x0B] ^ flag[0x0E] == 0x1C)
s.add(flag[0x1] ^ flag[0x4] == 0x2)
s.add(flag[0x12] ^ flag[0x15] == 0x2B)

s.check()
m = s.model()

dico = {}
for a in m:
	dico[int(str(a).split('flag_')[1])] = chr(m[a].as_long())

flag = ""
for a in range(48):
	flag += dico[a]

print(flag)