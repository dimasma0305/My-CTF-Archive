from random import randint

ROUNDS = 8
KEYS = [(randint(11, 19), randint(1, 200)) for _ in range(ROUNDS)]


def encode(number):
    """Feistel encryption operation to map 8-bit inputs to 8-bit outputs."""
    left, right = number >> 4, number & 0x0f
    for keynum in range(ROUNDS):
        left, right = right, left ^ feistel(right, keynum)
    return left << 4 | right


def decode(number):
    """Inverse (decryption) Feistel operation."""
    left, right = number >> 4, number & 0x0f
    for keynum in reversed(range(ROUNDS)):
        left, right = right ^ feistel(left, keynum), left
    return left << 4 | right


def feistel(number, keynum):
    """Feistel non-invertible transformation function,"""
    offset, multiplier = KEYS[keynum]
    return (number + offset) * multiplier & 0x0f


if __name__ == '__main__':
    assert [decode(encode(n)) for n in range(256)] == range(256)