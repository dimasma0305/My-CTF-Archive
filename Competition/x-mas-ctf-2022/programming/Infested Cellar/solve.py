"""
You will have to solve 20 tests in at most 40 seconds. Good luck!
Loading challenge...
In order to reduce output size, we generate the array of bugs with the following formula
v[i] = (a * v[i - 2] + b * v[i - 1] + c) % d, for all 2 <= i < N

Step: #1 out of 20!
N = 5; K = 3; P = 2; Q = 4
v[0] = 3; v[1] = 19
a = 14; b = 18; c = 9; d = 30
Ans = 
"""

v = [0]*5

N = 5; K = 3; P = 2; Q = 4
v[0] = 8; v[1] = 25
a = 1; b = 22; c = 8; d = 30

arr = [N, K, P, Q]

for idx_arr in range(2,5):
    v[idx_arr] = ((v[idx_arr - 2] * a + v[idx_arr - 1] * b + c) % d)

hari = 0
for hari in range(20):
    print(v, hari+1)
    hari += 1
    isSprayed = [0]*(len(v)+1)
    isSpray = [0]*(len(arr)+1)
    for idx_arr in range(len(arr)):
        for idx_barel in range(len(v)):
            if isSprayed[idx_barel] == 0:
                if v[idx_barel] == 0:
                    continue
                if arr[idx_arr] in v:
                    if isSpray[idx_arr] == 0:
                        for i in range(len(v)):
                            if v[i] == arr[idx_arr]:
                                v[i] -= arr[idx_arr]
                                isSpray[i] = 1
                                isSpray[idx_arr] = 1
                                break
                if v[idx_barel] >= arr[idx_arr]:
                    if isSpray[idx_arr] == 0:
                        v[idx_barel] -= arr[idx_arr]
                        isSprayed[idx_barel] = 1
                        isSpray[idx_arr] = 1