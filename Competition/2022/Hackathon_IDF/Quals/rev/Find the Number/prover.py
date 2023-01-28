from z3 import *

v7 = IntVector('v7', 6)
s = Solver()
s.add(v7[0] == 1337)
s.add(v7[2] == 1337 * v7[1])
s.add(v7[3] + v7[4] == 1337)
s.add(v7[5] - v7[3] == 10)
s.add(v7[5] == v7[4] + v7[0])
if s.check() == sat:
    m = s.model()
    print(m)