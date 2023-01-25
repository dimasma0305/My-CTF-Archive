import awik

final = b'ns@\xf5Y^\x12\x1fDb6.\x9f\xcb\x0b>\x98Se7\xc0\x10B\x1d^O+*\x88\xb4r\xa4\x81\x08\x88VV\xb8\xf1"a\x0bc\x91_;)\xd3\xfcB'
dump = ['?' for i in range(50)]
import string
for i in range(50):
    for j in 'CJ0123456789abcdefghijklmnopqrstuvwxyz_{}ABDEFGHIKLMNOPQRSTUVWXYZ':
        dump[i] = j
        inp = ''.join(dump)
        ret = awik.check(inp.encode())
        ret = eval(ret[:-1])
        print(".")
        if ret[i] == final[i]:
            print(dump[i], end='')
            break

print(''.join(dump))