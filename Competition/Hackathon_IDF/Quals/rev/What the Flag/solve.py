"""
  for ( i = 0; i < strlen(s); ++i )
  {
    if ( (v15[i] ^ (i + 10)) != s[i] )
    {
      puts("[!] Wrong!");
      exit(0);
    }
  }
"""
from pwn import xor


v15 = "irnh|xqc`z{gel]xibCO{iESQF{`jawQ\x1c\x1fsoGAQCKN"

flag = b""
for i in range(len(v15)):
    flag += xor(v15[i], i+10)

print(flag)
