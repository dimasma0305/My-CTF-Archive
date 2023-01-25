from pwn import *
from pprint import pprint

initial = [[1, 2,  3,  4,  5,  6,  7,  8, 9, 10],
           [11, 12, 13, 14, 15, 16, 17, 18, 19, 20],
           [21, 22, 23, 24, 25, 26, 27, 28, 29, 30],
           [31, 32, 33, 34, 35, 36, 37, 38, 39, 40],
           [41, 42, 43, 44, 45, 46, 47, 48, 49, 50],
           [51, 52, 53, 54, 55, 56, 57, 58, 59, 60],
           [61, 62, 63, 64, 65, 66, 67, 68, 69, 70],
           [71, 72, 73, 74, 75, 76, 77, 78, 79, 80],
           [81, 82, 83, 84, 85, 86, 87, 88, 89, 90],
           [91, 92, 93, 94, 95, 96, 97, 98, 99, 100]]

def is_positive(arr: list):
    min_idx = arr.index(min(arr))
    mid = len(arr) / 2
    if min_idx > mid:
        return True
    else:
        return False
    

def parse_rubik(rubik: str):
    _rubik = rubik.split()
    rubik_obj = []
    tmp = []
    for i, obj in enumerate(_rubik):
        tmp.append(int(obj))
        if (i+1) % 10 == 0:
            rubik_obj.append(tmp)
            tmp = []
    return rubik_obj


def permutate_left(rubik, y):
    tmp = 0
    for x in range(len(rubik[0])):
        if x == 0:
            tmp = rubik[x][y]
            rubik[y][x] = rubik[y][x+1]
        elif x == len(rubik) -1:
            rubik[y][x] = tmp
        else:
            rubik[y][x] = rubik[y][x+1]
def permutate_right(rubik, y):
    tmp = 0
    for x in reversed(range(len(rubik))):
        if x == len(rubik[0])-1:
            tmp = rubik[y][x]
            rubik[y][x] = rubik[y][x-1]
        elif x == 0:
            rubik[y][x] = tmp
        else:
            rubik[y][x] = rubik[y][x-1]
def permutate_up(rubik, x):
    tmp = 0
    for y in range(len(rubik)):
        if y == 0:
            tmp = rubik[y][x]
            rubik[y][x] = rubik[y+1][x]
        elif y == len(rubik)-1:
            rubik[y][x] = tmp
        else:
            rubik[y][x] = rubik[y+1][x]
            
def permutate_down(rubik, x):
    tmp = 0
    for y in reversed(range(len(rubik))):
        if y == len(rubik)-1:
            tmp = rubik[y][x]
            rubik[y][x] = rubik[y-1][x]
        elif y == 0:
            rubik[y][x] = tmp
        else:
            rubik[y][x] = rubik[y-1][x]

def step_y(rubik):
    count = 0
    for y in range(len(rubik)):
        if rubik[y][0] != initial[y][0]:
            if is_positive([rubik[y][i] for i in range(len(rubik[0]))]):
                while (rubik[y][0] != initial[y][0]):
                    permutate_right(rubik, y)
                    pprint(rubik)
                    count += 1
            else:
                while (rubik[y][0] != initial[y][0]):
                    permutate_left(rubik, y)
                    pprint(rubik)
                    count += 1
    return count
def step_x(rubik):
    count = 0
    for x in range(len(rubik[0])):
        if rubik[0][x] != initial[0][x]:
            if is_positive([rubik[i][x] for i in range(len(rubik))]):
                while ([rubik[0][x]] != initial[0][x]):
                    permutate_down(rubik, x)
                    pprint(rubik)
                    count += 1
            else:
                while ([rubik[0][x]] != initial[0][x]):
                    permutate_up(rubik, x)
                    pprint(rubik)
                    count += 1
    return count

def get_hindrance_idx(ls: list):
    def hindrance_idx():
        patern_poll:dict[int, list[list]] = dict()
        for i in range(len(ls)):
            if i == len(ls)-1:
                break
            tmp = ls[i] - ls[i+1]
            if tmp in patern_poll:
                patern_poll[tmp][0] += 1
                patern_poll[tmp][1].append(ls[i])
            else:
                patern_poll.update({tmp:[0, [ls[i]]]})

        top = 0
        for _, val in patern_poll.items():
            if val[0] <= top:
                if val[1].__len__() == 1:
                    return ls.index(val[1][0])+1
                return val
        return None
    return hindrance_idx()

def initial_step(rubik: list[list]):
    count = 0
    minimal = min(rubik[0] + [rubik[i][0] for i in range(len(rubik))])

    for y in range(len(rubik)):
        hindrance = get_hindrance_idx(rubik[y])
        if hindrance:
            print(hindrance)

    if minimal in rubik[0]:
        count += step_y(rubik)
    else:
        count += step_x(rubik)
    
    return count
                


r = remote("challs.htsp.ro", 14001)
r.recvuntil("10\n")
# 
rubik = r.recvuntil(b"Ans").replace(b"\nAns", b"").decode()
rubik = parse_rubik(rubik)

print(initial_step(rubik))
# r.interactive()
