import r2pipe

r = r2pipe.open('chall')
r.cmd("aa")
r.cmd("ood")
r.cmd("e dbg.profile=profile.rr2")
# print(r.cmd("pdf @ main"))
r.cmd("db 0x00401184")
r.cmd("dc")

print(r.cmd("pxr @ rsp"))