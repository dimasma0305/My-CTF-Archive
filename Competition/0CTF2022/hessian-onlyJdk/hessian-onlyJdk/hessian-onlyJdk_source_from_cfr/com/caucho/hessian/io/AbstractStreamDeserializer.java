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
public abstract class AbstractStreamDeserializer
extends AbstractDeserializer {
    @Override
    public abstract Class<?> getType();

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        Object value = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                value = this.readStreamValue(in);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        return value;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        Object value = null;
        for (int i = 0; i < fieldNames.length; ++i) {
            if ("value".equals(fieldNames[i])) {
                value = this.readStreamValue(in);
                in.addRef(value);
                continue;
            }
            in.readObject();
        }
        return value;
    }

    protected abstract Object readStreamValue(AbstractHessianInput var1) throws IOException;
}

