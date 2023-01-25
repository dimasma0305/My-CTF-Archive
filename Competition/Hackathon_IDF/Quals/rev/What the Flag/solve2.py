s = "irnh|xqc`z{gel]xibCO{iESQF{`jawQ"
flag = "".join([chr(ord(s[i]) ^ (i + 10)) for i in range(len(s))])
print(flag)