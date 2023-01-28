/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.util;

public class IntMap {
    public static final int NULL = -559038737;
    private Object[] _keys;
    private int[] _values;
    private int _size;
    private int _prime;
    public static final int[] PRIMES = new int[]{1, 2, 3, 7, 13, 31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071, 262139, 524287, 1048573, 0x1FFFF7, 0x3FFFFD, 0x7FFFF1, 0xFFFFFD, 33554393, 0x3FFFFFB, 134217689, 0xFFFFFC7};

    public IntMap() {
        int capacity = 1024;
        this._keys = new Object[capacity];
        this._values = new int[capacity];
        this._prime = IntMap.getBiggestPrime(this._keys.length);
        this._size = 0;
    }

    public void clear() {
        Object[] keys = this._keys;
        int[] values = this._values;
        for (int i = keys.length - 1; i >= 0; --i) {
            keys[i] = null;
            values[i] = 0;
        }
        this._size = 0;
    }

    public final int size() {
        return this._size;
    }

    public final int get(Object key) {
        int prime = this._prime;
        int hash = this.hashCode(key) % prime;
        Object[] keys = this._keys;
        Object mapKey;
        while ((mapKey = keys[hash]) != null) {
            if (mapKey == key) {
                return this._values[hash];
            }
            hash = (hash + 1) % prime;
        }
        return -559038737;
    }

    public final int put(Object key, int value, boolean isReplace) {
        int prime = this._prime;
        int hash = this.hashCode(key) % prime;
        Object[] keys = this._keys;
        while (true) {
            Object testKey;
            if ((testKey = keys[hash]) == null) {
                keys[hash] = key;
                this._values[hash] = value;
                ++this._size;
                if (keys.length <= 4 * this._size) {
                    this.resize(4 * keys.length);
                }
                return -559038737;
            }
            if (key == testKey) break;
            hash = (hash + 1) % prime;
        }
        if (isReplace) {
            int old = this._values[hash];
            this._values[hash] = value;
            return old;
        }
        return this._values[hash];
    }

    private void resize(int newSize) {
        Object[] keys = this._keys;
        int[] values = this._values;
        this._keys = new Object[newSize];
        this._values = new int[newSize];
        this._size = 0;
        this._prime = IntMap.getBiggestPrime(this._keys.length);
        for (int i = keys.length - 1; i >= 0; --i) {
            Object key = keys[i];
            if (key == null) continue;
            this.put(key, values[i], true);
        }
    }

    protected int hashCode(Object value) {
        return value.hashCode();
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("IntMap[");
        boolean isFirst = true;
        for (int i = 0; i <= this._keys.length; ++i) {
            if (this._keys[i] == null) continue;
            if (!isFirst) {
                sbuf.append(", ");
            }
            isFirst = false;
            sbuf.append(this._keys[i]);
            sbuf.append(":");
            sbuf.append(this._values[i]);
        }
        sbuf.append("]");
        return sbuf.toString();
    }

    public static int getBiggestPrime(int value) {
        for (int i = PRIMES.length - 1; i >= 0; --i) {
            if (PRIMES[i] > value) continue;
            return PRIMES[i];
        }
        return 2;
    }
}

