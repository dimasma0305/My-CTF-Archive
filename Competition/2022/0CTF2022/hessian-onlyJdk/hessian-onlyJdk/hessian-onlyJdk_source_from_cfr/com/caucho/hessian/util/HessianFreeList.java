/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class HessianFreeList<T> {
    private final AtomicReferenceArray<T> _freeStack;
    private final AtomicInteger _top = new AtomicInteger();

    public HessianFreeList(int size) {
        this._freeStack = new AtomicReferenceArray(size);
    }

    public T allocate() {
        int top = this._top.get();
        if (top > 0 && this._top.compareAndSet(top, top - 1)) {
            return this._freeStack.getAndSet(top - 1, null);
        }
        return null;
    }

    public boolean free(T obj) {
        int top = this._top.get();
        if (top < this._freeStack.length()) {
            boolean isFree = this._freeStack.compareAndSet(top, null, obj);
            this._top.compareAndSet(top, top + 1);
            return isFree;
        }
        return false;
    }

    public boolean allowFree(T obj) {
        return this._top.get() < this._freeStack.length();
    }

    public void freeCareful(T obj) {
        if (this.checkDuplicate(obj)) {
            throw new IllegalStateException("tried to free object twice: " + obj);
        }
        this.free(obj);
    }

    public boolean checkDuplicate(T obj) {
        int top = this._top.get();
        for (int i = top - 1; i >= 0; --i) {
            if (this._freeStack.get(i) != obj) continue;
            return true;
        }
        return false;
    }
}

