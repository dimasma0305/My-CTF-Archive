/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectDeserializer
extends AbstractDeserializer {
    private Class<?> _cl;

    public ObjectDeserializer(Class<?> cl) {
        this._cl = cl;
    }

    @Override
    public Class<?> getType() {
        return this._cl;
    }

    @Override
    public Object readObject(AbstractHessianInput in) throws IOException {
        return in.readObject();
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._cl + "]";
    }
}

