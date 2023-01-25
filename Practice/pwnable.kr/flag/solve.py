import r2pipe as r2
import os

def decode_upx():
    os.system("upx -d ./flag")

r = r2.open('./flag')
r.cmd('doo; db 0x0040119a')
r.cmd('dc')
print("Flag: {}".format(r.cmd('ps @ `dr rax`')))
