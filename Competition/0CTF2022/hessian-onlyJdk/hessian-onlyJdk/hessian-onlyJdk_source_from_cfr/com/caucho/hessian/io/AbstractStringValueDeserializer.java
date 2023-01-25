/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;

public abstract class AbstractStringValueDeserializer
extends AbstractDeserializer {
    protected abstract Object create(String var1) throws IOException;

    public Object readMap(AbstractHessianInput in) throws IOException {
        String value = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                value = in.readString();
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        Object object = this.create(value);
        in.addRef(object);
        return object;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        String value = null;
        for (int i = 0; i < fieldNames.length; ++i) {
            if ("value".equals(fieldNames[i])) {
                value = in.readString();
                continue;
            }
            in.readObject();
        }
        Object object = this.create(value);
        in.addRef(object);
        return object;
    }
}

