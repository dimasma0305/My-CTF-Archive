import re

with open("VaultDoor1.java", "r") as f:
    DATA = f.read()
    
def extract(data: str = DATA):
    charAt = re.findall(r"(?<=password.charAt\().*(?=\))", data)
    assert len(charAt) == 32
    charAt = [int(i) for i in charAt]
    char = re.findall(r'(?<=== \').*(?=\')', data)
    assert len(char) == 32
    lst = dict()
    for i in range(32):
        lst[charAt[i]] = char[i]
    n = sorted(lst.items(), key=lambda x:x, reverse=False)
    return"".join([i[1] for i in n])

def wrapFlag(txt):
    return "picoCTF{"+txt+"}"    

print(wrapFlag(extract()))
    
    