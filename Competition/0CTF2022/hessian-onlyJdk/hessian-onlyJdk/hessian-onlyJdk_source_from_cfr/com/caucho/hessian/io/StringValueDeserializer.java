/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractStringValueDeserializer;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class StringValueDeserializer
extends AbstractStringValueDeserializer {
    private Class _cl;
    private Constructor _constructor;

    public StringValueDeserializer(Class cl) {
        try {
            this._cl = cl;
            this._constructor = cl.getConstructor(String.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class getType() {
        return this._cl;
    }

    protected Object create(String value) throws IOException {
        if (value == null) {
            throw new IOException(this._cl.getName() + " expects name.");
        }
        try {
            return this._constructor.newInstance(value);
        }
        catch (Exception e) {
            throw new HessianException(this._cl.getName() + ": value=" + value + "\n" + e, e);
        }
    }
}

